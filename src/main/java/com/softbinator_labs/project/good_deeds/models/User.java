package com.softbinator_labs.project.good_deeds.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.*;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username can't be blank")
    @Pattern(regexp = "^[a-z|A-z][a-zA-Z0-9]{2,19}$", message = "Username should have at least 3 characters and at most 20. It should also start with a letter")
    @Column(unique = true, length = 20)
    private String username;

    @NotNull(message = "Email address can't be blank")
    @Email(message = "The address doesn't respect the format for an email address")
    @Column(unique = true, length = 320)
    private String email;

    @NotBlank(message = "Password can't be blank")
    @Size(min = 6, message = "Passwords must have at least 6 characters")
    @Column(length = 60)
    private String password;

    @NotBlank(message = "First name can't be blank")
    @Pattern(regexp = "^[A-Z][a-zA-Z\\s-]{2,29}$", message = "First name should have between 2 and 30 letters, starting with capital letter")
    @Column(name = "first_name", length = 30)
    private String firstName;

    @NotBlank(message = "Last name can't be blank")
    @Pattern(regexp = "^[A-Z][a-zA-Z\\s-]{2,29}$", message = "Last name should have between 2 and 30 letters, starting with capital letter")
    @Column(name = "last_name", length = 30)
    private String lastName;

    @NotBlank(message = "Phone number can't be blank")
    @Pattern(regexp = "^07\\d{8}$", message = "Phone number should start with 07 and it should have 10 digits")
    @Column(unique = true, length = 10)
    private String phone;

    @NotNull(message = "Number of points can't be a null object")
    @Min(value = 0, message = "Number of points can't be negative")
    private Integer points;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    private Boolean enabled;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GeneratedVoucher> vouchers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Volunteer> eventVolunteers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Donation> eventDonations = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
