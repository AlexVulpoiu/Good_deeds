package com.softbinator_labs.project.good_deeds.controllers;

import com.softbinator_labs.project.good_deeds.dtos.UpdateVolunteerStatusDto;
import com.softbinator_labs.project.good_deeds.dtos.VolunteerWithdrawDto;
import com.softbinator_labs.project.good_deeds.services.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/volunteers")
public class VolunteerController {

    private final VolunteerService volunteerService;

    @Autowired
    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @GetMapping("/viewAll")
    public ResponseEntity<?> getAllVolunteers() {
        return volunteerService.getAllVolunteers();
    }

    @GetMapping("/view/{eventId}")
    public ResponseEntity<?> getVolunteersForEvent(@PathVariable Long eventId) {
        return volunteerService.getVolunteersForEvent(eventId);
    }

    @GetMapping("/viewMyApplications")
    public ResponseEntity<?> getMyApplications() {
        return volunteerService.getMyApplications();
    }

    @PostMapping("/enroll/{eventId}")
    public ResponseEntity<?> enrollAtEvent(@PathVariable Long eventId, @RequestParam("file") MultipartFile file)
            throws MessagingException, UnsupportedEncodingException {
        return volunteerService.enrollAtEvent(eventId, file);
    }

    @PutMapping("/updateStatus")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody UpdateVolunteerStatusDto editVolunteer)
            throws MessagingException, UnsupportedEncodingException {
        return volunteerService.updateStatus(editVolunteer);
    }

    @PutMapping("/withdraw")
    public ResponseEntity<?> withdrawApplication(@Valid @RequestBody VolunteerWithdrawDto volunteerWithdrawDto)
            throws MessagingException, UnsupportedEncodingException {
        return volunteerService.withdrawApplication(volunteerWithdrawDto);
    }
}
