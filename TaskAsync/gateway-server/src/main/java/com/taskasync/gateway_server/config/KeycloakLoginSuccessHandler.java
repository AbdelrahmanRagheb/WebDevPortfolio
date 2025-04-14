package com.taskasync.gateway_server.config;

import com.taskasync.gateway_server.dto.UserSyncRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakLoginSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.user-service.uri:http://localhost:8082}")
    private String userServiceUri;


    private final ServerAuthenticationSuccessHandler delegate = new RedirectServerAuthenticationSuccessHandler();

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        log.info("Authentication successful for user: {}", authentication.getName());

        if (authentication instanceof OAuth2AuthenticationToken && authentication.getPrincipal() instanceof OidcUser oidcUser) {

            UserSyncRequest syncRequest = mapOidcUserToSyncRequest(oidcUser);


            callUserService(syncRequest)
                .doOnError(error -> log.error("Failed to sync user data to user-service for user {}: {}",
                        syncRequest.getKeycloakId(), error.getMessage()))
                .subscribe();


        } else {
            log.warn("Authentication type not O// In your Gateway project or a shared library\n" +
                    "package com.tasksync.gateway_server.dto;\n" +
                    "\n" +
                    "public class UserSyncRequest {\n" +
                    "    private String keycloakId; // From 'sub' claim\n" +
                    "    private String email;\n" +
                    "    private String firstName; // From 'given_name'\n" +
                    "    private String lastName;  // From 'family_name'\n" +
                    "    private String username; // From 'preferred_username'\n" +
                    "    // Add other fields as needed\n" +
                    "\n" +
                    "    // Constructors, Getters, Setters\n" +
                    "}IDC or principal type unexpected: {}", authentication.getClass().getName());
        }



        return this.delegate.onAuthenticationSuccess(webFilterExchange, authentication);
    }

    private UserSyncRequest mapOidcUserToSyncRequest(OidcUser oidcUser) {
        UserSyncRequest request = new UserSyncRequest();
        request.setKeycloakId(oidcUser.getSubject());
        request.setEmail(oidcUser.getEmail());
        request.setFirstName(oidcUser.getGivenName());
        request.setLastName(oidcUser.getFamilyName());
        request.setUsername(oidcUser.getPreferredUsername());

        log.debug("Mapped OidcUser to UserSyncRequest: {}", request);
        return request;
    }

    private Mono<Void> callUserService(UserSyncRequest syncRequest) {
        String targetUri = userServiceUri + "/api/users/sync";
        log.info("Calling user-service at {} for user {}", targetUri, syncRequest.getKeycloakId());

        return webClientBuilder.build().post()
                .uri(targetUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(syncRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Successfully synced user {} to user-service", syncRequest.getKeycloakId()))
                .onErrorResume(error -> {
                    log.error("Error calling user-service sync endpoint for user {}: Status={}, Body={}",
                            syncRequest.getKeycloakId(),
                            error instanceof org.springframework.web.reactive.function.client.WebClientResponseException ?
                                    ((org.springframework.web.reactive.function.client.WebClientResponseException) error).getStatusCode() : "N/A",
                            error instanceof org.springframework.web.reactive.function.client.WebClientResponseException ?
                                    ((org.springframework.web.reactive.function.client.WebClientResponseException) error).getResponseBodyAsString() : error.getMessage(),
                            error);
                    return Mono.empty();
                });
    }

}