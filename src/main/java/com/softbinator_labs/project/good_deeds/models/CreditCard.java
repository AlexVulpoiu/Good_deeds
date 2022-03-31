package com.softbinator_labs.project.good_deeds.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "credit_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 16, max = 16)
    @Pattern(regexp = "^[1-9]\\d{15}$")
    @Column(unique = true, name = "card_number", length = 16)
    private String cardNumber;

    @NotBlank
    @Min(100)
    @Max(999)
    private Integer cvv;

    @NotNull
    @Min(0)
    private Long balance;

    @NotBlank
    @Size(min = 2, max = 30)
    @Column(name = "owner_first_name", length = 30)
    private String ownerFirstName;

    @NotBlank
    @Size(min = 2, max = 30)
    @Column(name = "owner_last_name", length = 30)
    private String ownerLastName;

    @NotNull
    @Min(1)
    @Max(12)
    @Column(name = "expiration_month")
    private Integer expirationMonth;

    @NotNull
    @Min(2022)
    @Column(name = "expiration_year")
    private Integer expirationYear;
}
