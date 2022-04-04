package com.softbinator_labs.project.good_deeds.dtos;

import com.softbinator_labs.project.good_deeds.models.EStatus;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventVolunteerDto {

    @NotBlank
    @Size(min = 3, max = 30)
    private String firstName;

    @NotBlank
    @Size(min = 3, max = 30)
    private String lastName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EStatus status;
}
