package com.softbinator_labs.project.good_deeds.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
@Table(name = "volunteers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Volunteer {

    @EmbeddedId
    private VolunteerId id;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(length = 9)
    private EStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    private CharityEvent event;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Volunteer volunteer = (Volunteer) o;
        return id.equals(volunteer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
