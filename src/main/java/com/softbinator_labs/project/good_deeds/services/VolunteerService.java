package com.softbinator_labs.project.good_deeds.services;

import com.softbinator_labs.project.good_deeds.dtos.*;
import com.softbinator_labs.project.good_deeds.models.*;
import com.softbinator_labs.project.good_deeds.repositories.CharityEventRepository;
import com.softbinator_labs.project.good_deeds.repositories.UserRepository;
import com.softbinator_labs.project.good_deeds.repositories.VolunteerRepository;
import com.softbinator_labs.project.good_deeds.utils.KeycloakHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;

    private final CharityEventRepository charityEventRepository;

    private final UserRepository userRepository;

    private final JavaMailSender mailSender;

    @Autowired
    public VolunteerService(VolunteerRepository volunteerRepository, CharityEventRepository charityEventRepository, UserRepository userRepository, JavaMailSender mailSender) {
        this.volunteerRepository = volunteerRepository;
        this.charityEventRepository = charityEventRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    public ResponseEntity<?> getAllVolunteers() {

        List<Volunteer> volunteerList = volunteerRepository.findAll();
        if(volunteerList.isEmpty()) {
            return new ResponseEntity<>("There is no volunteer enrolled yet!", HttpStatus.OK);
        }

        return getVolunteerDtoList(volunteerList);
    }

    public ResponseEntity<?> getVolunteersForEvent(Long eventId) {

        Optional<CharityEvent> charityEvent = charityEventRepository.findById(eventId);
        if(charityEvent.isEmpty()) {
            return new ResponseEntity<>("The charity event with id " + eventId + " doesn't exist!", HttpStatus.NOT_FOUND);
        }

        List<Volunteer> volunteers = charityEvent.get().getUserVolunteers();
        return getVolunteerDtoList(volunteers);
    }

    private ResponseEntity<?> getVolunteerDtoList(List<Volunteer> volunteers) {

        List<VolunteerDto> volunteerDtoList = new ArrayList<>();
        for(Volunteer volunteer: volunteers) {
            User user = volunteer.getUser();
            UserInfoDto userInfoDto = UserInfoDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();

            String eventTitle = volunteer.getEvent().getTitle();
            String eventDescription = volunteer.getEvent().getDescription();
            VolunteerDto volunteerDto = VolunteerDto.builder()
                    .user(userInfoDto)
                    .eventTitle(eventTitle)
                    .eventDescription(eventDescription)
                    .status(volunteer.getStatus())
                    .build();
            volunteerDtoList.add(volunteerDto);
        }

        return new ResponseEntity<>(volunteerDtoList, HttpStatus.OK);
    }

    public ResponseEntity<?> getMyApplications() {

        User user = KeycloakHelper.getCurrentUser(userRepository);
        if(user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(user.getEventVolunteers().isEmpty()) {
            return new ResponseEntity<>("You haven't applied as a volunteer for an event yet!", HttpStatus.OK);
        }

        List<Volunteer> applications = user.getEventVolunteers();
        List<VolunteerApplicationDto> myApplications = new ArrayList<>();
        for(Volunteer volunteer: applications) {
            CharityEvent event = volunteer.getEvent();
            VolunteerApplicationDto volunteerApplicationDto = VolunteerApplicationDto.builder()
                    .status(volunteer.getStatus())
                    .title(event.getTitle())
                    .category(event.getCategory())
                    .description(event.getDescription())
                    .organisation(event.getOrganisation().getName())
                    .neededMoney(event.getNeededMoney())
                    .collectedMoney(event.getCollectedMoney())
                    .location(event.getLocation())
                    .startDate(event.getStartDate())
                    .endDate(event.getEndDate())
                    .volunteersNeeded(event.getVolunteersNeeded())
                    .acceptedVolunteers(event.getAcceptedVolunteers())
                    .cvRequired(event.getCvRequired()).build();
            myApplications.add(volunteerApplicationDto);
        }

        return new ResponseEntity<>(myApplications, HttpStatus.OK);
    }

    public ResponseEntity<?> enrollAtEvent(Long eventId, MultipartFile file)
            throws MessagingException, UnsupportedEncodingException {

        Optional<CharityEvent> charityEvent = charityEventRepository.findById(eventId);
        if(charityEvent.isEmpty()) {
            return new ResponseEntity<>("The charity event with id " + eventId + " doesn't exist!", HttpStatus.NOT_FOUND);
        }

        if(KeycloakHelper.currentUserIsAdmin()) {
            return new ResponseEntity<>("You can't enroll as a volunteer because you are an admin!",
                    HttpStatus.METHOD_NOT_ALLOWED);
        }

        User user = KeycloakHelper.getCurrentUser(userRepository);
        if(user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(user.getOrganisation() != null) {
            return new ResponseEntity<>("You can't enroll as a volunteer because you are an organiser!",
                    HttpStatus.METHOD_NOT_ALLOWED);
        }

        VolunteerId volunteerId = VolunteerId.builder()
                .eventId(charityEvent.get().getId())
                .userId(user.getId())
                .build();
        Optional<Volunteer> dbVolunteer = volunteerRepository.findById(volunteerId);
        if(dbVolunteer.isPresent()) {
            return new ResponseEntity<>("You have already applied as a volunteer for this event!", HttpStatus.OK);
        }

        String fileName = file.getOriginalFilename();
        try {
            file.transferTo(new File("C:\\Good_Deeds_Files\\" + fileName));
        } catch(Exception e) {
            return new ResponseEntity<>("File upload didn't succeeded", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        CharityEvent event = charityEvent.get();
        VolunteerId id = VolunteerId.builder()
                .userId(user.getId())
                .eventId(eventId)
                .build();
        Volunteer volunteer = Volunteer.builder()
                .id(id)
                .status(EStatus.SENT)
                .user(user)
                .event(event)
                .build();
        volunteerRepository.save(volunteer);
        event.addVolunteer(user, volunteer);

        String subject = "Your application was submitted!";
        String message = "This is a confirmation mail for your application at event " + event.getTitle();
        sendInformationMail(user, subject, message, null);

        subject = "You have received a new application!";
        message = "Details: <br>"
                + "- event name: " + event.getTitle() + "<br>"
                + "- period: " + event.getStartDate() + " -> " + event.getEndDate() + "<br>"
                + "- location: " + event.getLocation() + "<br>"
                + "- name: " + user.getFirstName() + " " + user.getLastName() + "<br>"
                + "- email: " + user.getEmail() + "<br>"
                + "- phone: " + user.getPhone() + "<br>";
        sendInformationMail(event.getOrganisation().getOwner(), subject, message, file);

        return new ResponseEntity<>("You successfully enrolled for this event!", HttpStatus.OK);
    }

    public ResponseEntity<?> updateStatus(@Valid UpdateVolunteerStatusDto editVolunteer)
            throws MessagingException, UnsupportedEncodingException {

        User user = KeycloakHelper.getCurrentUser(userRepository);
        if(user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Volunteer> volunteer = volunteerRepository.findById(editVolunteer.getId());
        if(volunteer.isEmpty()) {
            return new ResponseEntity<>("This volunteer doesn't exist", HttpStatus.NOT_FOUND);
        }

        Optional<CharityEvent> charityEvent = charityEventRepository.findById(volunteer.get().getEvent().getId());
        if(charityEvent.isEmpty()) {
            return new ResponseEntity<>("This volunteer doesn't exist", HttpStatus.NOT_FOUND);
        }

        if(user != charityEvent.get().getOrganisation().getOwner()) {
            return new ResponseEntity<>("You can't perform this action!", HttpStatus.FORBIDDEN);
        }

        CharityEvent event = charityEvent.get();
        Volunteer currentVolunteer = volunteer.get();
        if(currentVolunteer.getStatus() == EStatus.WITHDRAW || editVolunteer.getStatus() == EStatus.WITHDRAW) {
            return new ResponseEntity<>("You can't edit the status of this application because the user withdrew!",
                    HttpStatus.METHOD_NOT_ALLOWED);
        }

        if(currentVolunteer.getStatus() != EStatus.ACCEPTED && editVolunteer.getStatus() == EStatus.ACCEPTED) {
            event.setAcceptedVolunteers(event.getAcceptedVolunteers() + 1);
            charityEventRepository.save(event);
        }
        currentVolunteer.setStatus(editVolunteer.getStatus());
        volunteerRepository.save(currentVolunteer);

        UserInfoDto userInfoDto = UserInfoDto.builder()
                .id(currentVolunteer.getUser().getId())
                .username(currentVolunteer.getUser().getUsername())
                .email(currentVolunteer.getUser().getEmail())
                .build();
        VolunteerDto volunteerDto = VolunteerDto.builder()
                .user(userInfoDto)
                .eventTitle(event.getTitle())
                .eventDescription(event.getDescription())
                .status(currentVolunteer.getStatus())
                .build();

        String subject = "Your application status was updated!";
        String message = "The status for your volunteer application was updated to " + editVolunteer.getStatus();
        sendInformationMail(currentVolunteer.getUser(), subject, message, null);

        return new ResponseEntity<>(volunteerDto, HttpStatus.OK);
    }

    public ResponseEntity<?> withdrawApplication(@Valid VolunteerWithdrawDto volunteerWithdrawDto)
            throws MessagingException, UnsupportedEncodingException {

        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerWithdrawDto.getId());
        if(volunteer.isEmpty()) {
            return new ResponseEntity<>("This volunteer doesn't exist", HttpStatus.NOT_FOUND);
        }

        User user = KeycloakHelper.getCurrentUser(userRepository);
        if(user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // the user that requested this is not the same as the volunteer
        if(!Objects.equals(user.getId(), volunteerWithdrawDto.getId().getUserId())) {
            return new ResponseEntity<>("You can't perform this action!", HttpStatus.METHOD_NOT_ALLOWED);
        }

        Volunteer currentVolunteer = volunteer.get();
        if(currentVolunteer.getStatus() == EStatus.ACCEPTED) {
            CharityEvent event = currentVolunteer.getEvent();
            event.setAcceptedVolunteers(event.getAcceptedVolunteers() - 1);
            charityEventRepository.save(event);
        }

        currentVolunteer.setStatus(EStatus.WITHDRAW);
        volunteerRepository.save(currentVolunteer);

        String subject = "Your application status was updated!";
        String message = "This is a confirmation mail for your withdraw from event " + currentVolunteer.getEvent().getTitle();
        sendInformationMail(currentVolunteer.getUser(), subject, message, null);

        subject = "Withdraw information";
        message = "One of your volunteers has just withdraw. <br>" + "Additional info: <br>"
                + "- event name: " + currentVolunteer.getEvent().getTitle() + "<br>"
                + "- period: " + currentVolunteer.getEvent().getStartDate() + " -> " + currentVolunteer.getEvent().getEndDate() + "<br>"
                + "- location: " + currentVolunteer.getEvent().getLocation() + "<br>"
                + "- username: " + currentVolunteer.getUser().getUsername() + "<br>"
                + "- email: " + currentVolunteer.getUser().getEmail() + "<br>"
                + "- phone: " + currentVolunteer.getUser().getPhone() + "<br>"
                + "Here is the reason for this action: <br>" + volunteerWithdrawDto.getReason();
        sendInformationMail(currentVolunteer.getEvent().getOrganisation().getOwner(), subject, message, null);

        return new ResponseEntity<>("Your withdraw was successfully processed!", HttpStatus.OK);
    }

    private void sendInformationMail(User user, String subject, String message, MultipartFile file)
            throws MessagingException, UnsupportedEncodingException {

        String toAddress = user.getEmail();
        String fromAddress = "unibucbears@gmail.com";
        String senderName = "Good Deeds";
        String content = "Dear [[name]],<br>"
                + message + "<br>"
                + "Thank you,<br>"
                + "Good Deeds.";
        content = content.replace("[[name]]", user.getFirstName() + " " + user.getLastName());

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        if(file == null) {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(fromAddress, senderName);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(content, true);
        } else {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(fromAddress, senderName);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource fileSystemResource = new FileSystemResource(new File("C:\\Good_Deeds_Files\\" + file.getOriginalFilename()));
            helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), fileSystemResource);
        }

        mailSender.send(mimeMessage);
    }
}
