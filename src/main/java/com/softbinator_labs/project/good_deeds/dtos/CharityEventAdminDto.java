package com.softbinator_labs.project.good_deeds.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.softbinator_labs.project.good_deeds.models.ECategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharityEventAdminDto {

    @NotNull
    private Long id;

    @NotBlank
    @Size(min = 5, max = 30)
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ECategory category;

    @NotBlank
    @Size(min = 50, max = 1000)
    private String description;

    @NotBlank
    private String organisation;

    @NotBlank
    @Size(min = 7, max = 61)
    @JsonProperty("organisation_owner")
    private String organisationOwner;

    @NotNull
    @Min(0)
    @JsonProperty("needed_money")
    private Integer neededMoney;

    @NotNull
    @Min(0)
    @JsonProperty("collected_money")
    private Integer collectedMoney;

    @NotBlank
    @Size(min = 3, max = 100)
    private String location;

    @NotNull
    @Future
    @JsonProperty("start_date")
    private LocalDate startDate;

    @NotNull
    @Future
    @JsonProperty("end_date")
    private LocalDate endDate;

    @NotNull
    @Min(0)
    @JsonProperty("volunteers_needed")
    private Integer volunteersNeeded;

    @NotNull
    @Min(0)
    @JsonProperty("accepted_volunteers")
    private Integer acceptedVolunteers;

    @NotNull
    @JsonProperty("cv_required")
    private Boolean cvRequired;

    @NotNull
    private List<EventVolunteerDto> volunteers;
}
