package com.softbinator_labs.project.good_deeds.utils;

import com.softbinator_labs.project.good_deeds.models.CreditCard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddCreditCardsFromCSV {

    public static List<CreditCard> getCreditCards() throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader("src/main/java/com/softbinator_labs/project/good_deeds/utils/credit_cards.csv"));
        String line;

        List<CreditCard> creditCards = new ArrayList<>();

        reader.readLine(); // first line contains headers
        line = reader.readLine();
        while(line != null) {
            String[] values = line.split(",");

            for (int i = 0; i < values.length; i++) {
                values[i] = values[i].strip();
            }

            String cardNumber = SHA256.hash256(values[0]);
            String cvv = SHA256.hash256(values[1]);
            Integer balance = Integer.parseInt(values[2]);
            String firstName = SHA256.hash256(values[3]);
            String lastName = SHA256.hash256(values[4]);
            Integer expirationMonth = Integer.parseInt(values[5]);
            Integer expirationYear = Integer.parseInt(values[6]);

            CreditCard creditCard = CreditCard.builder()
                    .cardNumber(cardNumber)
                    .cvv(cvv)
                    .balance(balance)
                    .ownerFirstName(firstName)
                    .ownerLastName(lastName)
                    .expirationMonth(expirationMonth)
                    .expirationYear(expirationYear)
                    .build();

            creditCards.add(creditCard);

            line = reader.readLine();
        }

        return creditCards;
    }
}
