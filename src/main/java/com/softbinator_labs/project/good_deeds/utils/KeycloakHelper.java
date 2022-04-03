package com.softbinator_labs.project.good_deeds.utils;

import com.softbinator_labs.project.good_deeds.models.User;
import com.softbinator_labs.project.good_deeds.repositories.UserRepository;
import org.keycloak.KeycloakPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class KeycloakHelper {

    public static String getUser(Authentication authentication) {
        return ((KeycloakPrincipal<?>) authentication.getPrincipal()).getKeycloakSecurityContext()
                .getToken().getPreferredUsername();
    }

    public static User getCurrentUser(UserRepository userRepository) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = Long.valueOf(getUser(authentication));
        Optional<User> currentUser = userRepository.findById(id);
        if(currentUser.isEmpty()) {
            return null;
        }

        return currentUser.get();
    }
}
