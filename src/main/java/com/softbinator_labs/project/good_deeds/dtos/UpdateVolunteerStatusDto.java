package com.softbinator_labs.project.good_deeds.dtos;

import com.softbinator_labs.project.good_deeds.models.EStatus;
import com.softbinator_labs.project.good_deeds.models.VolunteerId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVolunteerStatusDto {

    @NotNull(message = "Please provide a valid id for the volunteer!")
    private VolunteerId id;

    @NotNull(message = "Please provide a valid status!")
    private EStatus status;
}
