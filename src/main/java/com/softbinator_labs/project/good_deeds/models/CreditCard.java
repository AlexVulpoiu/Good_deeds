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
    @Size(min = 64, max = 64)
    @Column(unique = true, name = "card_number", length = 64)
    private String cardNumber;

    @NotBlank
    @Size(min = 64, max = 64)
    @Column(length = 64)
    private String cvv;

    @NotNull
    @Min(0)
    private Integer balance;

    @NotBlank
    @Size(min = 64, max = 64)
    @Column(name = "owner_first_name", length = 64)
    private String ownerFirstName;

    @NotBlank
    @Size(min = 64, max = 64)
    @Column(name = "owner_last_name", length = 64)
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
