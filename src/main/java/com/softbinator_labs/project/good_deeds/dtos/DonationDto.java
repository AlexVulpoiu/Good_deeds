package com.softbinator_labs.project.good_deeds.dtos;

import com.softbinator_labs.project.good_deeds.models.DonationId;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationDto {

    @NotNull
    private DonationId id;

    @NotNull
    @Min(1)
    private Integer amount;

    @NotNull
    private UserInfoDto user;
}
