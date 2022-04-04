package com.softbinator_labs.project.good_deeds.controllers;

import com.softbinator_labs.project.good_deeds.dtos.NewCharityEventDto;
import com.softbinator_labs.project.good_deeds.models.CharityEvent;
import com.softbinator_labs.project.good_deeds.services.CharityEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/charityEvents")
public class CharityEventController {

    private final CharityEventService charityEventService;

    @Autowired
    public CharityEventController(CharityEventService charityEventService) {
        this.charityEventService = charityEventService;
    }

    @GetMapping("/viewAll")
    public ResponseEntity<?> getAllEvents() {
        return charityEventService.getAllEvents();
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<?> getEvent(@PathVariable Long id) {
        return charityEventService.getEvent(id);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEvent(@Valid @RequestBody NewCharityEventDto newCharityEvent) {
        return charityEventService.addEvent(newCharityEvent);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editEvent(@PathVariable Long id, @Valid @RequestBody NewCharityEventDto editCharityEvent) {
        return charityEventService.editEvent(id, editCharityEvent);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        return charityEventService.deleteEvent(id);
    }
}
