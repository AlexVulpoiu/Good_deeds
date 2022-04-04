package com.softbinator_labs.project.good_deeds.models;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "charity_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 5, max = 30)
    @Column(length = 30, unique = true)
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 6)
    private ECategory category;

    @NotBlank
    @Size(min = 50, max = 1000)
    @Column(length = 1000)
    private String description;

    @NotNull
    @Min(0)
    @Column(name = "needed_money")
    private Integer neededMoney;

    @NotNull
    @Min(0)
    @Column(name = "collected_money")
    private Integer collectedMoney;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(length = 100)
    private String location;

    @NotNull
    @Future
    @Column(name = "start_date")
    private LocalDate startDate;

    @NotNull
    @Future
    @Column(name = "end_date")
    private LocalDate endDate;

    @NotNull
    @Min(0)
    @Column(name = "volunteers_needed")
    private Integer volunteersNeeded;

    @NotNull
    @Min(0)
    @Column(name = "accepted_volunteers")
    private Integer acceptedVolunteers;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organisation organisation;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Volunteer> userVolunteers = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Donation> userDonations = new ArrayList<>();

    public void addVolunteer(User user, Volunteer volunteer) {
        userVolunteers.add(volunteer);
        user.getEventVolunteers().add(volunteer);
    }

    public void addDonation(User user, Integer amount) {
        Donation donation = new Donation(user, this, amount);
        userDonations.add(donation);
        user.getEventDonations().add(donation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharityEvent that = (CharityEvent) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
