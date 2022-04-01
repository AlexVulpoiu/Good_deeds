package com.softbinator_labs.project.good_deeds.services;

import com.softbinator_labs.project.good_deeds.dtos.RegisterUserDto;
import com.softbinator_labs.project.good_deeds.dtos.TokenDto;
import com.softbinator_labs.project.good_deeds.dtos.UserInfoDto;
import com.softbinator_labs.project.good_deeds.models.User;
import com.softbinator_labs.project.good_deeds.repositories.UserRepository;
import lombok.SneakyThrows;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.BadRequestException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final KeycloakAdminService keycloakAdminService;

    private final PasswordEncoder encoder;

    private final JavaMailSender mailSender;

    @Autowired
    public UserService(UserRepository userRepository, KeycloakAdminService keycloakAdminService, PasswordEncoder encoder, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.keycloakAdminService = keycloakAdminService;
        this.encoder = encoder;
        this.mailSender = mailSender;
    }

    public UserInfoDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist!"));

        return UserInfoDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @SneakyThrows
    public TokenDto registerUser(RegisterUserDto registerUserDto, String userRole, String siteURL) {

        if(userRepository.existsByUsername(registerUserDto.getUsername())) {
            throw new BadRequestException("User with username " + registerUserDto.getUsername() + " already exists!");
        }

        if(userRepository.existsByEmail(registerUserDto.getEmail())) {
            throw new BadRequestException("User with email " + registerUserDto.getEmail() + " already exists!");
        }

        if(userRepository.existsByPhone(registerUserDto.getPhone())) {
            throw new BadRequestException("User with phone " + registerUserDto.getPhone() + " already exists!");
        }

        User newUser = User.builder()
                .username(registerUserDto.getUsername())
                .email(registerUserDto.getEmail())
                .password(encoder.encode(registerUserDto.getPassword()))
                .firstName(registerUserDto.getFirstName())
                .lastName(registerUserDto.getLastName())
                .phone(registerUserDto.getPhone())
                .points(0)
                .build();

        String randomCode = RandomString.make(64);
        newUser.setVerificationCode(randomCode);
        newUser.setEnabled(false);

        Long userId = userRepository.save(newUser).getId();

        sendVerificationEmail(newUser, siteURL);

        return keycloakAdminService.addUserToKeycloak(userId, registerUserDto.getPassword(), userRole);
    }

    private void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {

        String toAddress = user.getEmail();
        String fromAddress = "unibucbears@gmail.com";
        String senderName = "Good Deeds";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Good Deeds.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFirstName() + " " + user.getLastName());
        String verifyURL = siteURL + "/users/verify?code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }

    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);

        if (user == null || user.getEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepository.save(user);

            return true;
        }

    }
}
