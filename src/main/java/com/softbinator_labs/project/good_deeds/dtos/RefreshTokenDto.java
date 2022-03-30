package com.softbinator_labs.project.good_deeds.dtos;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDto {

    @NotNull
    private String refreshToken;

    @NotNull
    private String grantType;
}
