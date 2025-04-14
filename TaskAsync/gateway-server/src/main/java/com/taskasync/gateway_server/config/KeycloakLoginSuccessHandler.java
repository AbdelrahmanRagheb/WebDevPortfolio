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
@RequiredArgsConstructor // Lombok for constructor injection
public class KeycloakLoginSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final WebClient.Builder webClientBuilder;
    // Optional: Inject if you need access tokens for UserInfo endpoint
    // private final ReactiveOAuth2AuthorizedClientService authorizedClientService;

    // Get the user-service URI (use service discovery if available)
    @Value("${services.user-service.uri:http://localhost:8082}") // Example: Load from properties or use default
    private String userServiceUri;

    // Use the default handler for redirection after our logic
    private final ServerAuthenticationSuccessHandler delegate = new RedirectServerAuthenticationSuccessHandler();

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        log.info("Authentication successful for user: {}", authentication.getName());

        if (authentication instanceof OAuth2AuthenticationToken && authentication.getPrincipal() instanceof OidcUser oidcUser) {
            // 1. Extract data primarily from ID Token (OidcUser)
            UserSyncRequest syncRequest = mapOidcUserToSyncRequest(oidcUser);

            // 2. Call User Service (asynchronously)
            callUserService(syncRequest)
                .doOnError(error -> log.error("Failed to sync user data to user-service for user {}: {}",
                        syncRequest.getKeycloakId(), error.getMessage()))
                .subscribe(); // Trigger the call, handle errors but don't block login flow

            // --- Optional: Call UserInfo endpoint if needed ---
            /*
            String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
            Mono<OAuth2AuthorizedClient> authorizedClientMono = authorizedClientService.loadAuthorizedClient(registrationId, authentication.getName());

            return authorizedClientMono.flatMap(authorizedClient -> {
                        String userInfoEndpointUri = authorizedClient.getClientRegistration()
                                .getProviderDetails().getUserInfoEndpoint().getUri();
                        String accessToken = authorizedClient.getAccessToken().getTokenValue();

                        return webClientBuilder.build().get()
                                .uri(userInfoEndpointUri)
                                .headers(headers -> headers.setBearerAuth(accessToken))
                                .retrieve()
                                .bodyToMono(Map.class) // Or a specific UserInfo DTO
                                .doOnNext(userInfo -> log.info("Fetched UserInfo: {}", userInfo))
                                .map(userInfo -> enrichSyncRequestFromUserInfo(syncRequest, userInfo)) // Add data from UserInfo
                                .flatMap(this::callUserService)
                                .then(delegate.onAuthenticationSuccess(webFilterExchange, authentication)); // Proceed with redirection AFTER UserInfo call
                    })
                    .onErrorResume(e -> {
                        log.error("Error fetching UserInfo or calling user-service: {}", e.getMessage());
                        // Decide if login should proceed even if sync fails
                        return delegate.onAuthenticationSuccess(webFilterExchange, authentication);
                    });
            */
            // --- End Optional UserInfo ---

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

        // 3. Delegate to default handler to perform the redirect
        // This happens immediately if not waiting for UserInfo call
        return this.delegate.onAuthenticationSuccess(webFilterExchange, authentication);
    }

    private UserSyncRequest mapOidcUserToSyncRequest(OidcUser oidcUser) {
        UserSyncRequest request = new UserSyncRequest();
        request.setKeycloakId(oidcUser.getSubject()); // 'sub' claim
        request.setEmail(oidcUser.getEmail());
        request.setFirstName(oidcUser.getGivenName());
        request.setLastName(oidcUser.getFamilyName());
        request.setUsername(oidcUser.getPreferredUsername());
        // Map other claims as needed...
        log.debug("Mapped OidcUser to UserSyncRequest: {}", request);
        return request;
    }

    private Mono<Void> callUserService(UserSyncRequest syncRequest) {
        String targetUri = userServiceUri + "/api/users/sync"; // Define your user-service endpoint
        log.info("Calling user-service at {} for user {}", targetUri, syncRequest.getKeycloakId());

        return webClientBuilder.build().post()
                .uri(targetUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(syncRequest)
                .retrieve()
                .bodyToMono(Void.class) // Or expect a response if needed
                .doOnSuccess(v -> log.info("Successfully synced user {} to user-service", syncRequest.getKeycloakId()))
                .onErrorResume(error -> {
                    log.error("Error calling user-service sync endpoint for user {}: Status={}, Body={}",
                            syncRequest.getKeycloakId(),
                            error instanceof org.springframework.web.reactive.function.client.WebClientResponseException ?
                                    ((org.springframework.web.reactive.function.client.WebClientResponseException) error).getStatusCode() : "N/A",
                            error instanceof org.springframework.web.reactive.function.client.WebClientResponseException ?
                                    ((org.springframework.web.reactive.function.client.WebClientResponseException) error).getResponseBodyAsString() : error.getMessage(),
                            error); // Log details
                    return Mono.empty(); // Don't let user-service failure break the login flow by default
                });
    }

    // Optional: Helper method if you fetch from UserInfo
    /*
    private UserSyncRequest enrichSyncRequestFromUserInfo(UserSyncRequest request, Map<String, Object> userInfo) {
         // Extract additional fields from userInfo map and add them to the request DTO
         // e.g., request.setProfilePictureUrl((String) userInfo.get("picture"));
         return request;
    }
    */
}