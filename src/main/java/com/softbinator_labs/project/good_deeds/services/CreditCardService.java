package com.softbinator_labs.project.good_deeds.services;

import com.softbinator_labs.project.good_deeds.models.CreditCard;
import com.softbinator_labs.project.good_deeds.repositories.CreditCardRepository;
import com.softbinator_labs.project.good_deeds.utils.AddCreditCardsFromCSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Service
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;

    @Autowired
    public CreditCardService(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    public ResponseEntity<String> addCreditCards() throws IOException {

        Collection<CreditCard> creditCards = creditCardRepository.findAll();
        if(creditCards.isEmpty()) {
            List<CreditCard> newCreditCards = AddCreditCardsFromCSV.getCreditCards();
            creditCardRepository.saveAll(newCreditCards);
            return new ResponseEntity<>("Credit cards successfully saved!", HttpStatus.OK);
        }

        return new ResponseEntity<>("Credit cards are already in the database!", HttpStatus.OK);
    }
}
