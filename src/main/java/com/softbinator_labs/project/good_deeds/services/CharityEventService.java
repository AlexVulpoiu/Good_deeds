package com.softbinator_labs.project.good_deeds.services;

import com.softbinator_labs.project.good_deeds.dtos.CharityEventAdminDto;
import com.softbinator_labs.project.good_deeds.dtos.CharityEventDto;
import com.softbinator_labs.project.good_deeds.dtos.EventVolunteerDto;
import com.softbinator_labs.project.good_deeds.dtos.NewCharityEventDto;
import com.softbinator_labs.project.good_deeds.models.CharityEvent;
import com.softbinator_labs.project.good_deeds.models.Organisation;
import com.softbinator_labs.project.good_deeds.models.User;
import com.softbinator_labs.project.good_deeds.models.Volunteer;
import com.softbinator_labs.project.good_deeds.repositories.CharityEventRepository;
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
public class CharityEventService {

    private final CharityEventRepository charityEventRepository;

    private final OrganisationRepository organisationRepository;

    private final UserRepository userRepository;

    @Autowired
    public CharityEventService(CharityEventRepository charityEventRepository, OrganisationRepository organisationRepository, UserRepository userRepository) {
        this.charityEventRepository = charityEventRepository;
        this.organisationRepository = organisationRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> getAllEvents() {

        Collection<CharityEvent> charityEvents = charityEventRepository.findAll();
        if(charityEvents.isEmpty()) {
            return new ResponseEntity<>("There is no charity event yet!", HttpStatus.OK);
        }

        List<CharityEventDto> charityEventDtoList = new ArrayList<>();
        for(CharityEvent charityEvent: charityEvents) {
            CharityEventDto charityEventDto = CharityEventDto.builder()
                    .id(charityEvent.getId())
                    .title(charityEvent.getTitle())
                    .category(charityEvent.getCategory())
                    .description(charityEvent.getDescription())
                    .organisation(charityEvent.getOrganisation().getName())
                    .organisationOwner(charityEvent.getOrganisation().getOwner().getFirstName() + " "
                            + charityEvent.getOrganisation().getOwner().getLastName())
                    .neededMoney(charityEvent.getNeededMoney())
                    .collectedMoney(0)
                    .location(charityEvent.getLocation())
                    .startDate(charityEvent.getStartDate())
                    .endDate(charityEvent.getEndDate())
                    .volunteersNeeded(charityEvent.getVolunteersNeeded())
                    .acceptedVolunteers(0)
                    .cvRequired(charityEvent.getCvRequired()).build();
            charityEventDtoList.add(charityEventDto);
        }

        return new ResponseEntity<>(charityEventDtoList, HttpStatus.OK);
    }

    public ResponseEntity<?> getEvent(Long id) {

        Optional<CharityEvent> charityEvent = charityEventRepository.findById(id);
        if(charityEvent.isEmpty()) {
            return new ResponseEntity<>("The charity event with id " + id + " doesn't exist!", HttpStatus.NOT_FOUND);
        }

        CharityEvent currentCharityEvent = charityEvent.get();
        User user = KeycloakHelper.getCurrentUser(userRepository);
        if(KeycloakHelper.currentUserIsAdmin() || currentCharityEvent.getOrganisation().getOwner().equals(user)) {
            return getCharityEventAdminDto(currentCharityEvent);
        }

        return getCharityEventDto(currentCharityEvent);
    }

    public ResponseEntity<?> addEvent(NewCharityEventDto newCharityEvent) {

        Optional<CharityEvent> event = charityEventRepository.findByTitle(newCharityEvent.getTitle());
        if(event.isPresent()) {
            return new ResponseEntity<>("There is already an event with the title you provided! Please choose another title!", HttpStatus.BAD_REQUEST);
        }

        User user = KeycloakHelper.getCurrentUser(userRepository);
        if(user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Organisation organisation = user.getOrganisation();

        CharityEvent charityEvent = CharityEvent.builder()
                .title(newCharityEvent.getTitle())
                .category(newCharityEvent.getCategory())
                .description(newCharityEvent.getDescription())
                .neededMoney(newCharityEvent.getNeededMoney())
                .collectedMoney(0)
                .location(newCharityEvent.getLocation())
                .startDate(newCharityEvent.getStartDate())
                .endDate(newCharityEvent.getEndDate())
                .volunteersNeeded(newCharityEvent.getVolunteersNeeded())
                .acceptedVolunteers(0)
                .cvRequired(newCharityEvent.getCvRequired())
                .organisation(organisation)
                .build();
        charityEventRepository.save(charityEvent);

        organisation.getEvents().add(charityEvent);
        organisationRepository.save(organisation);

        return new ResponseEntity<>("Charity event successfully added!", HttpStatus.OK);
    }

    public ResponseEntity<?> editEvent(Long id, NewCharityEventDto editCharityEvent) {

        Optional<CharityEvent> charityEvent = charityEventRepository.findById(id);
        if(charityEvent.isEmpty()) {
            return new ResponseEntity<>("The charity event with id " + id + " doesn't exist!", HttpStatus.NOT_FOUND);
        }

        CharityEvent currentCharityEvent = charityEvent.get();

        User organiser = currentCharityEvent.getOrganisation().getOwner();
        User user = KeycloakHelper.getCurrentUser(userRepository);
        if(organiser != user) {
            return new ResponseEntity<>("You can't access this endpoint!", HttpStatus.FORBIDDEN);
        }

        currentCharityEvent.setTitle(editCharityEvent.getTitle());
        currentCharityEvent.setCategory(editCharityEvent.getCategory());
        currentCharityEvent.setDescription(editCharityEvent.getDescription());
        currentCharityEvent.setNeededMoney(editCharityEvent.getNeededMoney());
        currentCharityEvent.setLocation(editCharityEvent.getLocation());
        currentCharityEvent.setStartDate(editCharityEvent.getStartDate());
        currentCharityEvent.setEndDate(editCharityEvent.getEndDate());
        currentCharityEvent.setVolunteersNeeded(editCharityEvent.getVolunteersNeeded());
        currentCharityEvent.setCvRequired(editCharityEvent.getCvRequired());
        charityEventRepository.save(currentCharityEvent);

        return getCharityEventAdminDto(currentCharityEvent);
    }

    public ResponseEntity<?> deleteEvent(Long id) {

        Optional<CharityEvent> charityEvent = charityEventRepository.findById(id);
        if(charityEvent.isEmpty()) {
            return new ResponseEntity<>("The charity event with id " + id + " doesn't exist!", HttpStatus.NOT_FOUND);
        }

        CharityEvent currentCharityEvent = charityEvent.get();
        User user = KeycloakHelper.getCurrentUser(userRepository);
        if(!currentCharityEvent.getOrganisation().getOwner().equals(user) && !KeycloakHelper.currentUserIsAdmin()) {
            return new ResponseEntity<>("You are not allowed to do this action!", HttpStatus.FORBIDDEN);
        }

        Organisation organisation = currentCharityEvent.getOrganisation();
        organisation.getEvents().remove(currentCharityEvent);

        currentCharityEvent.setOrganisation(null);
        charityEventRepository.delete(currentCharityEvent);

        return new ResponseEntity<>("Charity event successfully deleted!", HttpStatus.OK);
    }

    private ResponseEntity<?> getCharityEventDto(CharityEvent currentCharityEvent) {

        Organisation organisation = currentCharityEvent.getOrganisation();
        String organisationOwner = organisation.getOwner().getFirstName() + " " + organisation.getOwner().getLastName();

        CharityEventDto charityEventDto = CharityEventDto.builder()
                .id(currentCharityEvent.getId())
                .title(currentCharityEvent.getTitle())
                .category(currentCharityEvent.getCategory())
                .description(currentCharityEvent.getDescription())
                .organisation(currentCharityEvent.getOrganisation().getName())
                .organisationOwner(organisationOwner)
                .neededMoney(currentCharityEvent.getNeededMoney())
                .collectedMoney(currentCharityEvent.getCollectedMoney())
                .location(currentCharityEvent.getLocation())
                .startDate(currentCharityEvent.getStartDate())
                .endDate(currentCharityEvent.getEndDate())
                .volunteersNeeded(currentCharityEvent.getVolunteersNeeded())
                .acceptedVolunteers(currentCharityEvent.getAcceptedVolunteers())
                .cvRequired(currentCharityEvent.getCvRequired()).build();

        return new ResponseEntity<>(charityEventDto, HttpStatus.OK);
    }

    private ResponseEntity<?> getCharityEventAdminDto(CharityEvent currentCharityEvent) {

        Organisation organisation = currentCharityEvent.getOrganisation();
        String organisationOwner = organisation.getOwner().getFirstName() + " " + organisation.getOwner().getLastName();

        List<Volunteer> volunteers = currentCharityEvent.getUserVolunteers();
        List<EventVolunteerDto> eventVolunteers = new ArrayList<>();

        for(Volunteer volunteer: volunteers) {
            EventVolunteerDto eventVolunteerDto = EventVolunteerDto.builder()
                    .firstName(volunteer.getUser().getFirstName())
                    .lastName(volunteer.getUser().getLastName())
                    .status(volunteer.getStatus())
                    .build();
            eventVolunteers.add(eventVolunteerDto);
        }

        CharityEventAdminDto charityEventDto = CharityEventAdminDto.builder()
                .id(currentCharityEvent.getId())
                .title(currentCharityEvent.getTitle())
                .category(currentCharityEvent.getCategory())
                .description(currentCharityEvent.getDescription())
                .organisation(currentCharityEvent.getOrganisation().getName())
                .organisationOwner(organisationOwner)
                .neededMoney(currentCharityEvent.getNeededMoney())
                .collectedMoney(currentCharityEvent.getCollectedMoney())
                .location(currentCharityEvent.getLocation())
                .startDate(currentCharityEvent.getStartDate())
                .endDate(currentCharityEvent.getEndDate())
                .volunteersNeeded(currentCharityEvent.getVolunteersNeeded())
                .acceptedVolunteers(currentCharityEvent.getAcceptedVolunteers())
                .cvRequired(currentCharityEvent.getCvRequired())
                .volunteers(eventVolunteers).build();

        return new ResponseEntity<>(charityEventDto, HttpStatus.OK);
    }
}
