package com.softbinator_labs.project.good_deeds.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDto {

    @NotBlank
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^\\w{3,20}$")
    private String username;

    @NotNull
    @Email
    private String email;

    @NotBlank
    @Size(min = 120, max = 120)
    private String password;

    @NotBlank
    @Size(min = 2, max = 30)
    @JsonProperty(value = "first_name")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 30)
    @JsonProperty(value = "last_name")
    private String lastName;

    @NotBlank
    @Size(min = 10, max = 10)
    @Pattern(regexp = "^07\\d{8}$")
    private String phone;

    private Set<String> roles;
}
