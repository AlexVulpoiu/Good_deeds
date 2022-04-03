package com.softbinator_labs.project.good_deeds.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.softbinator_labs.project.good_deeds.models.CharityEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganisationAdminDto {

    @NotBlank
    @Size(min = 3, max = 30)
    private String name;

    @NotNull
    @JsonProperty("owner_id")
    private Long ownerId;

    private Set<CharityEvent> events;
}
