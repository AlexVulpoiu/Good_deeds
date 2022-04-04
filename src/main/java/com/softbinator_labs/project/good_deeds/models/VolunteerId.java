package com.softbinator_labs.project.good_deeds.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class VolunteerId implements Serializable {

    @Column(name = "user_id")
    @JsonProperty("user_id")
    private Long userId;

    @Column(name = "event_id")
    @JsonProperty("event_id")
    private Long eventId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VolunteerId that = (VolunteerId) o;
        return userId.equals(that.userId) && eventId.equals(that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, eventId);
    }
}
