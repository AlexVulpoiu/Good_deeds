package com.softbinator_labs.project.good_deeds.controllers;

import com.softbinator_labs.project.good_deeds.dtos.NewDonationDto;
import com.softbinator_labs.project.good_deeds.services.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/donations")
public class DonationController {

    private final DonationService donationService;

    @Autowired
    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @GetMapping("/view/{eventId}")
    public ResponseEntity<?> getDonationsForEvent(@PathVariable Long eventId) {
        return donationService.getDonationsForEvent(eventId);
    }

    @PostMapping("/makeDonation/{eventId}")
    public ResponseEntity<?> makeDonation(@PathVariable Long eventId, @Valid @RequestBody NewDonationDto newDonationDto) {
        return donationService.makeDonation(eventId, newDonationDto);
    }
}
