package com.softbinator_labs.project.good_deeds.models;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class DonationId implements Serializable {

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @Column(name = "event_id")
    private Long eventId;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime hour;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DonationId that = (DonationId) o;
        return userId.equals(that.userId) && eventId.equals(that.eventId) && date.equals(that.date) && hour.equals(that.hour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, eventId, date, hour);
    }
}
