package com.softbinator_labs.project.good_deeds.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherDto {

    @NotNull
    private Long id;

    @NotNull
    @Min(1)
    @Max(99)
    private Integer discount;

    @NotBlank
    @Size(min = 3, max = 30)
    @Column(length = 30)
    private String company;

    @NotNull
    @Min(1)
    private Integer price;
}
