package com.softbinator_labs.project.good_deeds.services;

import com.softbinator_labs.project.good_deeds.clients.AuthClient;
import com.softbinator_labs.project.good_deeds.dtos.TokenDto;
import com.softbinator_labs.project.good_deeds.models.User;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

@Service
public class KeycloakAdminService {

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.resource}")
    private String keycloakClient;

    private final Keycloak keycloak;

    private final AuthClient authClient;

    private RealmResource realm;

    public KeycloakAdminService(Keycloak keycloak, AuthClient authClient) {
        this.keycloak = keycloak;
        this.authClient = authClient;
    }

    @PostConstruct
    public void initRealmResource() {
        this.realm = this.keycloak.realm(keycloakRealm);
    }

    public TokenDto addUserToKeycloak(Long userId, String password, String userRole) {
        // create a new user
        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setEnabled(true);
        keycloakUser.setUsername(userId.toString());

        // variable for password
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();

        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        credentialRepresentation.setTemporary(false);

        keycloakUser.setCredentials(Collections.singletonList(credentialRepresentation));

        // create user in realm and get its id
        Response response = realm.users().create(keycloakUser);
        String keycloakUserId = getCreatedId(response);
        System.out.println("User has been saved with an id: " + keycloakUserId);

        // add ROLE_USER to the user
        UserResource userResource = realm.users().get(keycloakUserId);
        RoleRepresentation userRoleRepresentation = realm.roles().get("ROLE_USER").toRepresentation();
        if(userRole.equals("ROLE_ADMIN")) {
            RoleRepresentation adminRoleRepresentation = realm.roles().get("ROLE_ADMIN").toRepresentation();
            userResource.roles().realmLevel().add(List.of(userRoleRepresentation, adminRoleRepresentation));
        } else {
            userResource.roles().realmLevel().add(Collections.singletonList(userRoleRepresentation));
        }

        // login request for getting token
        MultiValueMap<String, String> loginCredentials = new LinkedMultiValueMap<>();
        loginCredentials.add("client_id", keycloakClient);
        loginCredentials.add("username", userId.toString());
        loginCredentials.add("password", password);
        loginCredentials.add("grant_type", "password");

        return authClient.login(loginCredentials);
    }

    public void addOrganiserRole() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String keycloakUserId = ((KeycloakPrincipal<?>) authentication.getPrincipal()).getName();
        UserResource userResource = realm.users().get(keycloakUserId);
        RoleRepresentation organiserRoleRepresentation = realm.roles().get("ROLE_ORGANISER").toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(organiserRoleRepresentation));
    }

    public void removeOrganiserRole(User user) {

        if(user == null) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String keycloakUserId = ((KeycloakPrincipal<?>) authentication.getPrincipal()).getName();
            UserResource userResource = realm.users().get(keycloakUserId);
            RoleRepresentation organiserRoleRepresentation = realm.roles().get("ROLE_ORGANISER").toRepresentation();
            userResource.roles().realmLevel().remove(Collections.singletonList(organiserRoleRepresentation));
        } else {

            List<UserRepresentation> userRepresentations = realm.users().search(String.valueOf(user.getId()));
            String keycloakUserId = userRepresentations.get(0).getId();
            UserResource userResource = realm.users().get(keycloakUserId);
            RoleRepresentation organiserRoleRepresentation = realm.roles().get("ROLE_ORGANISER").toRepresentation();
            userResource.roles().realmLevel().remove(Collections.singletonList(organiserRoleRepresentation));
        }
    }
}
