package com.softbinator_labs.project.good_deeds.models;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
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
    @Column(length = 30)
    private String title;

    @NotBlank
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
    @Column(name = "start_date")
    private LocalDate startDate;

    @NotNull
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

    @NotNull
    @Column(name = "cv_required")
    private Boolean cvRequired;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organisation organisation;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Volunteer> userVolunteers = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Donation> userDonations = new ArrayList<>();

    public void addVolunteer(User user) {
        Volunteer volunteer = Volunteer.builder().event(this).user(user).status(EStatus.CV_SENT).build();
        userVolunteers.add(volunteer);
        user.getEventVolunteers().add(volunteer);
    }

    public void removeVolunteer(User user) {
        for(Iterator<Volunteer> iterator = userVolunteers.iterator(); iterator.hasNext();) {
            Volunteer volunteer = iterator.next();

            if(volunteer.getUser().equals(user) && volunteer.getEvent().equals(this)) {
                iterator.remove();
                volunteer.getEvent().getUserVolunteers().remove(volunteer);
                volunteer.setUser(null);
                volunteer.setEvent(null);
            }
        }
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
