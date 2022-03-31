package com.softbinator_labs.project.good_deeds.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "donations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donation {

    @EmbeddedId
    private DonationId id;

    @NotNull
    @Min(1)
    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    private CharityEvent event;

    public Donation(User user, CharityEvent charityEvent, Integer amount) {
        DonationId id = new DonationId(user.getId(), charityEvent.getId(), LocalDate.now(), LocalTime.now());
        this.amount = amount;
        this.user = user;
        this.event = charityEvent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Donation donation = (Donation) o;
        return id.equals(donation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
