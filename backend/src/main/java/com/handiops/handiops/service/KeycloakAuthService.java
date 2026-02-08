package com.handiops.handiops.service;

import com.handiops.handiops.config.KeycloakConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;


import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAuthService {

    private final KeycloakConfig keycloakConfig;
    private final RestTemplate restTemplate;

    /**
     * Authenticate user with Keycloak using username and password
     */
    public Map<String, Object> authenticateUser(String username, String password) {
        try {
            String tokenEndpoint = keycloakConfig.getTokenEndpoint();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", "password");
            requestBody.add("client_id", keycloakConfig.getClientId());
            requestBody.add("client_secret", keycloakConfig.getClientSecret());
            requestBody.add("username", username);
            requestBody.add("password", password);
            requestBody.add("scope", "openid");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenEndpoint,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("User {} authenticated successfully", username);
                return response.getBody();
            } else {
                log.error("Authentication failed for user: {}", username);
                throw new RuntimeException("Authentication failed");
            }
        } catch (Exception e) {
            log.error("Error authenticating user: {}", e.getMessage());
            throw new RuntimeException("Authentication error: " + e.getMessage());
        }
    }

    /**
     * Refresh access token using refresh token
     */
    public Map<String, Object> refreshToken(String refreshToken) {
        try {
            String tokenEndpoint = keycloakConfig.getTokenEndpoint();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", "refresh_token");
            requestBody.add("client_id", keycloakConfig.getClientId());
            requestBody.add("client_secret", keycloakConfig.getClientSecret());
            requestBody.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenEndpoint,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Token refreshed successfully");
                return response.getBody();
            } else {
                log.error("Token refresh failed");
                throw new RuntimeException("Token refresh failed");
            }
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            throw new RuntimeException("Token refresh error: " + e.getMessage());
        }
    }

    /**
     * Get user info from Keycloak
     */

// داخل السيرفس الخاصة بك
public Map<String, Object> getUserInfo(String accessToken) {
    String userInfoEndpoint = keycloakConfig.getUserInfoEndpoint();
    log.info("Requesting user info from: {}", userInfoEndpoint);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);

    HttpEntity<String> request = new HttpEntity<>(headers);

    try {
        ResponseEntity<Map> response = restTemplate.exchange(
                userInfoEndpoint,
                HttpMethod.GET,
                request,
                Map.class
        );
        return response.getBody();

    } catch (HttpClientErrorException e) {
        // هام جداً: هذا السطر سيطبع لك السبب الحقيقي من Keycloak
        // مثلاً سيقول: "Missing openid scope"
        log.error("Keycloak Error Body: {}", e.getResponseBodyAsString());
        throw new RuntimeException("Keycloak Error: " + e.getResponseBodyAsString());
        
    } catch (Exception e) {
        log.error("General Error: {}", e.getMessage());
        throw new RuntimeException("Internal Auth Error");
    }
}

    /**
     * Logout user from Keycloak
     */
    public void logoutUser(String refreshToken) {
        try {
            String logoutEndpoint = keycloakConfig.getLogoutEndpoint();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("client_id", keycloakConfig.getClientId());
            requestBody.add("client_secret", keycloakConfig.getClientSecret());
            requestBody.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Void> response = restTemplate.exchange(
                    logoutEndpoint,
                    HttpMethod.POST,
                    request,
                    Void.class
            );

            if (response.getStatusCode() == HttpStatus.NO_CONTENT || response.getStatusCode() == HttpStatus.OK) {
                log.info("User logged out successfully");
            } else {
                log.error("Logout failed");
                throw new RuntimeException("Logout failed");
            }
        } catch (Exception e) {
            log.error("Error logging out user: {}", e.getMessage());
            throw new RuntimeException("Logout error: " + e.getMessage());
        }
    }
}
