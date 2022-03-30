package com.softbinator_labs.project.good_deeds.utils;

import org.keycloak.KeycloakPrincipal;
import org.springframework.security.core.Authentication;

public class KeycloakHelper {

    public static String getUser(Authentication authentication) {
        return ((KeycloakPrincipal<?>) authentication.getPrincipal()).getKeycloakSecurityContext()
                .getToken().getPreferredUsername();
    }
}
