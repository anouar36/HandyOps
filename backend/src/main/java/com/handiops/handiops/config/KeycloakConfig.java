package com.handiops.handiops.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class KeycloakConfig {

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    public String getRealm() {
        return realm;
    }

    public String getAuthServerUrl() {
        return authServerUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getTokenEndpoint() {
        return authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    public String getUserInfoEndpoint() {
        return authServerUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo";
    }

    public String getLogoutEndpoint() {
        return authServerUrl + "/realms/" + realm + "/protocol/openid-connect/logout";
    }
}
