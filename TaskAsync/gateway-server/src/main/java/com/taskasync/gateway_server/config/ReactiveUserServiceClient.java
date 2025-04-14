package com.taskasync.gateway_server.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class ReactiveUserServiceClient {

    private static final Logger log = LoggerFactory.getLogger(ReactiveUserServiceClient.class);
    private final WebClient webClient;

    public ReactiveUserServiceClient(WebClient.Builder webClientBuilder,
                                     ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        log.info("Initializing ReactiveUserServiceClient with load-balanced URL: lb://user-service");
        this.webClient = webClientBuilder
                .filter(lbFunction)
                .baseUrl("lb://user-service")
                .build();
    }

    public Mono<Boolean> checkUserExists(String keycloakId) {
        log.debug("Checking existence for Keycloak ID: {}", keycloakId);
        return webClient.get()
                .uri("/api/users/keycloak/{keycloakId}", keycloakId)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().is4xxClientError()) {
                        log.debug("User with Keycloak ID '{}' not found.", keycloakId);
                        return Mono.just(false);
                    }
                    log.error("Error checking user '{}': {}", keycloakId, ex.getMessage(), ex);
                    return Mono.error(ex);
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Unexpected error checking user '{}': {}", keycloakId, ex.getMessage(), ex);
                    return Mono.error(ex);
                });
    }

    public Mono<Long> createUser(CreateUserRequest request) {
        log.debug("Creating user for Keycloak ID: {}", request.getKeycloakSubjectId());
        return webClient.post()
                .uri("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Long.class) // Expect just the database ID
                .doOnSuccess(databaseId -> log.info("User created with DB ID: {}", databaseId))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("User service error creating user '{}': Status {}, Body {}",
                            request.getKeycloakSubjectId(), ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
                    return Mono.error(ex);
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Unexpected error during create user call for '{}': {}", request.getKeycloakSubjectId(), ex.getMessage(), ex);
                    return Mono.error(ex);
                });
    }

    public Mono<Long> getDatabaseId(String keycloakId) {
        log.debug("Fetching database ID for Keycloak ID: {}", keycloakId);
        return webClient.get()
                .uri("/api/users/keycloak/{keycloakId}/id", keycloakId)
                .retrieve()
                .bodyToMono(Long.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Failed to fetch DB ID for '{}': Status {}, Body {}",
                            keycloakId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
                    return Mono.just(-1L); // Fallback value
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Unexpected error fetching DB ID for '{}': {}", keycloakId, ex.getMessage(), ex);
                    return Mono.just(-1L);
                });
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateUserRequest {
        private String keycloakSubjectId;
        private String email;
        private String username;
        private String firstName;
        private String lastName;
        private String fullName;
    }
}