package com.softbinator_labs.project.good_deeds.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewDonationDto {

    @NotNull(message = "You should provide a valid integer for donation!")
    @Min(value = 1, message = "You can't donate a negative or a float sum!")
    private Integer amount;

    @NotBlank(message = "Card number should contain exactly 16 digits!")
    @Size(min = 16, max = 16, message = "Card number should contain exactly 16 digits!")
    @JsonProperty("card_number")
    private String cardNumber;

    @NotNull(message = "You should provide a valid number for cvv!")
    @Min(value = 100, message = "Cvv should have exactly 3 digits!")
    @Max(value = 999, message = "Cvv should have exactly 3 digits!")
    private Integer cvv;

    @NotNull(message = "You should provide a valid expiration month!")
    @Min(value = 1, message = "Expiration month should be an integer greater or equal to 1!")
    @Max(value = 12, message = "Expiration month should be an integer smaller or equal to 12!")
    @JsonProperty("expiration_month")
    private Integer expirationMonth;

    @NotNull(message = "You should provide a valid expiration year!")
    @JsonProperty("expiration_year")
    private Integer expirationYear;

    @NotBlank(message = "Please provide card holder's first name!")
    @Pattern(regexp = "^[A-Z][a-zA-Z\\s-]{2,29}$",
            message = "First name should have between 3 and 30 letters, starting with capital letter!")
    @JsonProperty("owner_first_name")
    private String ownerFirstName;

    @NotBlank(message = "Please provide card holder's last name!")
    @Pattern(regexp = "^[A-Z][a-zA-Z\\s-]{2,29}$",
            message = "Last name should have between 3 and 30 letters, starting with capital letter!")
    @JsonProperty("owner_last_name")
    private String ownerLastName;
}
