package com.softbinator_labs.project.good_deeds.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDto {

    @NotBlank(message = "Username can't be blank")
    @Pattern(regexp = "^[a-z|A-z][a-zA-Z0-9]{2,19}$", message = "Username should have at least 3 characters and at most 20. It should also start with a letter")
    private String username;

    @NotNull(message = "Email address can't be blank")
    @Email(message = "The address doesn't respect the format for an email address")
    private String email;

    @NotBlank(message = "Password can't be blank")
    @Size(min = 6, message = "Passwords must have at least 6 characters")
    private String password;

    @NotBlank(message = "First name can't be blank")
    @Pattern(regexp = "^[A-Z][a-zA-Z\\s-]{2,29}$", message = "First name should have between 2 and 30 letters, starting with capital letter")
    @JsonProperty(value = "first_name")
    private String firstName;

    @NotBlank(message = "Last name can't be blank")
    @Pattern(regexp = "^[A-Z][a-zA-Z\\s-]{2,29}$", message = "Last name should have between 2 and 30 letters, starting with capital letter")
    @JsonProperty(value = "last_name")
    private String lastName;

    @NotBlank(message = "Phone number can't be blank")
    @Pattern(regexp = "^07\\d{8}$", message = "Phone number should start with 07 and it should have 10 digits")
    private String phone;
}
