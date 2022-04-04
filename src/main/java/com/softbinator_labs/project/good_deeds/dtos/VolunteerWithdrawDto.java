package com.softbinator_labs.project.good_deeds.dtos;

import com.softbinator_labs.project.good_deeds.models.VolunteerId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerWithdrawDto {

    @NotNull(message = "Please provide the id of your application!")
    private VolunteerId id;

    @NotBlank(message = "Please provide a reason for your withdraw!")
    @Size(min = 50, max = 1000, message = "Your message should have between 50 and 1000 characters!")
    private String reason;
}
