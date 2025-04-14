package com.taskasync.gateway_server.config;


import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final ReactiveUserServiceClient userServiceClient;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(exchange -> exchange
                        .pathMatchers("/task-service/default", "/actuator/**", "/gateway-server/default", "/configserver/**").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oAuth2ResourceServerSpec ->
                        oAuth2ResourceServerSpec.jwt(Customizer.withDefaults()))
                .oauth2Login(login -> login
                        .authenticationSuccessHandler(oidcAuthenticationSuccessHandler()))
                .addFilterAfter(userDataPropagationFilter(), SecurityWebFiltersOrder.AUTHENTICATION); // Fixed order

        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }

    @Bean
    public ServerAuthenticationSuccessHandler oidcAuthenticationSuccessHandler() {
        RedirectServerAuthenticationSuccessHandler redirectHandler = new RedirectServerAuthenticationSuccessHandler();

        return (webFilterExchange, authentication) -> {
            log.debug("Authentication successful.");
            if (!(authentication instanceof OAuth2AuthenticationToken) || !(authentication.getPrincipal() instanceof OidcUser)) {
                log.warn("Authentication token is not OIDC.");
                return redirectHandler.onAuthenticationSuccess(webFilterExchange, authentication);
            }

            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            String keycloakId = oidcUser.getSubject();
            if (keycloakId == null) {
                log.error("Keycloak subject ID missing.");
                return Mono.error(new IllegalStateException("Keycloak subject ID not found."));
            }

            log.info("User '{}' authenticated via Keycloak.", keycloakId);

            return userServiceClient.checkUserExists(keycloakId)
                    .flatMap(exists -> {
                        if (Boolean.TRUE.equals(exists)) {
                            log.info("User '{}' exists in user-service.", keycloakId);
                            return fetchDatabaseIdAndProceed(webFilterExchange, authentication, keycloakId, redirectHandler);
                        } else {
                            log.info("Creating user '{}' in user-service.", keycloakId);
                            ReactiveUserServiceClient.CreateUserRequest request = new ReactiveUserServiceClient.CreateUserRequest(
                                    keycloakId, oidcUser.getEmail(), oidcUser.getPreferredUsername(),
                                    oidcUser.getGivenName(), oidcUser.getFamilyName(), oidcUser.getFullName()
                            );
                            return userServiceClient.createUser(request)
                                    .flatMap(databaseId -> {
                                        webFilterExchange.getExchange().getAttributes().put("databaseId", databaseId);
                                        log.info("User '{}' created with DB ID: {}", keycloakId, databaseId);
                                        return redirectHandler.onAuthenticationSuccess(webFilterExchange, authentication);
                                    })
                                    .onErrorResume(ex -> {
                                        log.error("Failed to create user '{}': {}", keycloakId, ex.getMessage(), ex);
                                        return redirectHandler.onAuthenticationSuccess(webFilterExchange, authentication);
                                    });
                        }
                    })
                    .onErrorResume(ex -> {
                        log.error("Error checking user '{}': {}", keycloakId, ex.getMessage(), ex);
                        return redirectHandler.onAuthenticationSuccess(webFilterExchange, authentication);
                    });
        };
    }

    private Mono<Void> fetchDatabaseIdAndProceed(org.springframework.security.web.server.WebFilterExchange webFilterExchange,
                                                 org.springframework.security.core.Authentication authentication,
                                                 String keycloakId,
                                                 RedirectServerAuthenticationSuccessHandler redirectHandler) {
        return userServiceClient.getDatabaseId(keycloakId)
                .doOnSuccess(databaseId -> {
                    webFilterExchange.getExchange().getAttributes().put("databaseId", databaseId);
                    log.info("Fetched DB ID {} for existing user '{}'.", databaseId, keycloakId);
                })
                .onErrorResume(ex -> {
                    log.error("Failed to fetch DB ID for '{}': {}", keycloakId, ex.getMessage(), ex);
                    webFilterExchange.getExchange().getAttributes().put("databaseId", -1L); // Fallback value
                    return Mono.just(-1L);
                })
                .then(redirectHandler.onAuthenticationSuccess(webFilterExchange, authentication));
    }

    @Bean
    public WebFilter userDataPropagationFilter() {
        return (exchange, chain) -> {
            return exchange.getPrincipal()
                    .filter(principal -> principal instanceof OAuth2AuthenticationToken)
                    .cast(OAuth2AuthenticationToken.class)
                    .flatMap(auth -> {
                        OidcUser oidcUser = (OidcUser) auth.getPrincipal();
                        String keycloakId = oidcUser.getSubject();
                        Long databaseId = (Long) exchange.getAttributes().get("databaseId");
                        // For subsequent requests, fetch if not present
                        Mono<Long> databaseIdMono = databaseId != null ? Mono.just(databaseId)
                                : userServiceClient.getDatabaseId(keycloakId)
                                .doOnSuccess(id -> exchange.getAttributes().put("databaseId", id))
                                .onErrorResume(ex -> {
                                    log.error("Failed to fetch DB ID for '{}': {}", keycloakId, ex.getMessage(), ex);
                                    return Mono.just(-1L); // Fallback
                                });

                        return databaseIdMono.map(id -> {
                            ServerWebExchange mutatedExchange = exchange.mutate()
                                    .request(r -> r.headers(headers -> {
                                        headers.add("X-User-Id", keycloakId);
                                        headers.add("X-User-DatabaseId", id.toString());
                                        headers.add("X-User-Email", oidcUser.getEmail());
                                        headers.add("X-User-Username", oidcUser.getPreferredUsername());
                                        headers.add("X-User-FirstName", oidcUser.getGivenName());
                                        headers.add("X-User-LastName", oidcUser.getFamilyName());
                                        headers.add("X-User-FullName", oidcUser.getFullName());
                                        headers.add("X-User-EmailVerified", String.valueOf(oidcUser.getEmailVerified()));
                                    }))
                                    .build();
                            return mutatedExchange;
                        }).flatMap(chain::filter);
                    })
                    .switchIfEmpty(chain.filter(exchange));
        };
    }
}