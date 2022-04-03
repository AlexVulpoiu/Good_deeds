package com.softbinator_labs.project.good_deeds.controllers;

import com.softbinator_labs.project.good_deeds.models.Organisation;
import com.softbinator_labs.project.good_deeds.services.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/organisations")
public class OrganisationController {

    private final OrganisationService organisationService;

    @Autowired
    public OrganisationController(OrganisationService organisationService) {
        this.organisationService = organisationService;
    }

    @GetMapping("/viewAll")
    public ResponseEntity<?> getAllOrganisations() {
        return organisationService.getAllOrganisations();
    }

    @GetMapping("/myOrganisation")
    public ResponseEntity<?> getMyOrganisation() {
        return organisationService.getMyOrganisation();
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<?> getOrganisation(@PathVariable Long id) {
        return organisationService.getOrganisation(id);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addOrganisation(@Valid @RequestBody Organisation newOrganisation) {
        return organisationService.addOrganisation(newOrganisation);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editOrganisation(@Valid @RequestBody Organisation editOrganisation) {
        return organisationService.editOrganisation(editOrganisation);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMyOrganisation() {
        return organisationService.deleteMyOrganisation();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrganisation(@PathVariable Long id) {
        return organisationService.deleteOrganisation(id);
    }
}
