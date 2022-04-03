package com.softbinator_labs.project.good_deeds.dtos;

import com.softbinator_labs.project.good_deeds.models.CharityEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganisationDto {

    @NotBlank
    @Size(min = 3, max = 30)
    private String name;

    private Set<CharityEvent> events;
}
