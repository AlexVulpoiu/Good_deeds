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

    @NotBlank
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^\\w{3,20}$")
    @Column(unique = true, length = 20)
    private String username;

    @NotNull
    @Email
    @Column(length = 320)
    private String email;

    @NotBlank
    @Size(min = 120, max = 120)
    @Column(length = 120)
    private String password;

    @NotBlank
    @Size(min = 2, max = 30)
    @Column(name = "first_name", length = 30)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 30)
    @Column(name = "last_name", length = 30)
    private String lastName;

    @NotNull
    private Integer points;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

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
