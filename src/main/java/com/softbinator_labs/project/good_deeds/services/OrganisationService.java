package com.softbinator_labs.project.good_deeds.services;

import com.softbinator_labs.project.good_deeds.dtos.OrganisationAdminDto;
import com.softbinator_labs.project.good_deeds.dtos.OrganisationDto;
import com.softbinator_labs.project.good_deeds.models.Organisation;
import com.softbinator_labs.project.good_deeds.models.User;
import com.softbinator_labs.project.good_deeds.repositories.OrganisationRepository;
import com.softbinator_labs.project.good_deeds.repositories.UserRepository;
import com.softbinator_labs.project.good_deeds.utils.KeycloakHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class OrganisationService {

    private final OrganisationRepository organisationRepository;

    private final UserRepository userRepository;

    private final KeycloakAdminService keycloakAdminService;

    @Autowired
    public OrganisationService(OrganisationRepository organisationRepository, UserRepository userRepository, KeycloakAdminService keycloakAdminService) {
        this.organisationRepository = organisationRepository;
        this.userRepository = userRepository;
        this.keycloakAdminService = keycloakAdminService;
    }

    public ResponseEntity<?> getAllOrganisations() {

        Collection<Organisation> organisations = organisationRepository.findAll();
        if(organisations.isEmpty()) {
            return new ResponseEntity<>("There is no organisation created yet!", HttpStatus.OK);
        }

        List<OrganisationAdminDto> organisationAdminDtos = new ArrayList<>();
        for(Organisation organisation: organisations) {
            OrganisationAdminDto organisationAdminDto = OrganisationAdminDto.builder()
                    .name(organisation.getName())
                    .ownerId(organisation.getOwner().getId())
                    .events(organisation.getEvents()).build();
            organisationAdminDtos.add(organisationAdminDto);
        }

        return new ResponseEntity<>(organisationAdminDtos, HttpStatus.OK);
    }

    public ResponseEntity<?> getMyOrganisation() {

        User user = KeycloakHelper.getCurrentUser(userRepository);
        if(user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Organisation organisation = user.getOrganisation();
        if(organisation == null) {
            return new ResponseEntity<>("You haven't created an organisation yet!", HttpStatus.OK);
        }

        OrganisationDto organisationDto = OrganisationDto.builder()
                .name(organisation.getName())
                .events(organisation.getEvents()).build();

        return new ResponseEntity<>(organisationDto, HttpStatus.OK);
    }

    public ResponseEntity<?> getOrganisation(Long id) {

        Optional<Organisation> organisation = organisationRepository.findById(id);
        if(organisation.isEmpty()) {
            return new ResponseEntity<>("The organisation with id " + id + " doesn't exist!", HttpStatus.NOT_FOUND);
        }

        Organisation currentOrganisation = organisation.get();
        OrganisationAdminDto organisationAdminDto = OrganisationAdminDto.builder()
                .name(currentOrganisation.getName())
                .ownerId(currentOrganisation.getOwner().getId())
                .events(currentOrganisation.getEvents()).build();

        return new ResponseEntity<>(organisationAdminDto, HttpStatus.OK);
    }

    public ResponseEntity<?> addOrganisation(Organisation newOrganisation) {

        User user = KeycloakHelper.getCurrentUser(userRepository);
        if(user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(user.getOrganisation() != null) {
            return new ResponseEntity<>("You can own only one organisation!", HttpStatus.FORBIDDEN);
        }

        Optional<Organisation> currentOrganisation = organisationRepository.findByName(newOrganisation.getName());
        if(currentOrganisation.isPresent()) {
            return new ResponseEntity<>("There is already an organisation with this name! Please choose another name for your organisation!", HttpStatus.BAD_REQUEST);
        }

        keycloakAdminService.addOrganiserRole();
        Organisation organisation = Organisation.builder()
                .name(newOrganisation.getName())
                .owner(user)
                .build();
        user.setOrganisation(organisation);
        userRepository.save(user);

        return new ResponseEntity<>("Organisation successfully added!", HttpStatus.OK);
    }

    public ResponseEntity<?> editOrganisation(Organisation editOrganisation) {

        User user = KeycloakHelper.getCurrentUser(userRepository);
        if(user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(user.getOrganisation() == null) {
            return new ResponseEntity<>("You are not allowed to access this endpoint!", HttpStatus.METHOD_NOT_ALLOWED);
        }

        Optional<Organisation> dbOrganisation = organisationRepository.findByName(editOrganisation.getName());
        if(dbOrganisation.isPresent() && !dbOrganisation.get().getOwner().equals(user)) {
            return new ResponseEntity<>("There is already an organisation with this name! Please choose another name for your organisation!", HttpStatus.BAD_REQUEST);
        }

        Organisation organisation = user.getOrganisation();
        organisation.setName(editOrganisation.getName());
        organisationRepository.save(organisation);

        user.setOrganisation(organisation);
        userRepository.save(user);

        OrganisationDto organisationDto = OrganisationDto.builder()
                .name(organisation.getName())
                .events(organisation.getEvents()).build();

        return new ResponseEntity<>(organisationDto, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteMyOrganisation() {

        User user = KeycloakHelper.getCurrentUser(userRepository);
        if(user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(user.getOrganisation() == null) {
            return new ResponseEntity<>("You are not allowed to access this endpoint!", HttpStatus.METHOD_NOT_ALLOWED);
        }

        keycloakAdminService.removeOrganiserRole(null);
        Long organisationId = user.getOrganisation().getId();
        Optional<Organisation> currentOrganisation = organisationRepository.findById(organisationId);
        if(currentOrganisation.isEmpty()) {
            return new ResponseEntity<>("You are not allowed to access this endpoint!", HttpStatus.METHOD_NOT_ALLOWED);
        }

        user.setOrganisation(null);
        userRepository.save(user);

        Organisation organisation = currentOrganisation.get();
        organisation.setOwner(null);
        organisationRepository.delete(organisation);

        return new ResponseEntity<>("Organisation successfully deleted!", HttpStatus.OK);
    }

    public ResponseEntity<?> deleteOrganisation(Long id) {

        Optional<Organisation> organisation = organisationRepository.findById(id);
        if(organisation.isEmpty()) {
            return new ResponseEntity<>("The organisation with id " + id + " doesn't exist!", HttpStatus.NOT_FOUND);
        }

        User user = organisation.get().getOwner();
        keycloakAdminService.removeOrganiserRole(user);

        user.setOrganisation(null);
        userRepository.save(user);

        Organisation currentOrganisation = organisation.get();
        currentOrganisation.setOwner(null);
        organisationRepository.delete(currentOrganisation);

        return new ResponseEntity<>("Organisation successfully deleted!", HttpStatus.OK);
    }
}
