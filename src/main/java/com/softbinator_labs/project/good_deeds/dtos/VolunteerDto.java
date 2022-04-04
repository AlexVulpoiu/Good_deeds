package com.softbinator_labs.project.good_deeds.dtos;

import com.softbinator_labs.project.good_deeds.models.EStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerDto {

    @NotNull
    private UserInfoDto user;

    @NotBlank
    private String eventTitle;

    @NotBlank
    private String eventDescription;

    @NotNull
    private EStatus status;
}
