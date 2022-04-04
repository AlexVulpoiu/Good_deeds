package com.softbinator_labs.project.good_deeds.services;

import com.softbinator_labs.project.good_deeds.dtos.DonationDto;
import com.softbinator_labs.project.good_deeds.dtos.NewDonationDto;
import com.softbinator_labs.project.good_deeds.dtos.UserInfoDto;
import com.softbinator_labs.project.good_deeds.models.*;
import com.softbinator_labs.project.good_deeds.repositories.CharityEventRepository;
import com.softbinator_labs.project.good_deeds.repositories.CreditCardRepository;
import com.softbinator_labs.project.good_deeds.repositories.DonationRepository;
import com.softbinator_labs.project.good_deeds.repositories.UserRepository;
import com.softbinator_labs.project.good_deeds.utils.KeycloakHelper;
import com.softbinator_labs.project.good_deeds.utils.SHA256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DonationService {

    private final DonationRepository donationRepository;

    private final CharityEventRepository charityEventRepository;

    private final CreditCardRepository creditCardRepository;

    private final UserRepository userRepository;

    @Autowired
    public DonationService(DonationRepository donationRepository, CharityEventRepository charityEventRepository, CreditCardRepository creditCardRepository, UserRepository userRepository) {
        this.donationRepository = donationRepository;
        this.charityEventRepository = charityEventRepository;
        this.creditCardRepository = creditCardRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> makeDonation(Long eventId, NewDonationDto newDonationDto) {

        Optional<CharityEvent> charityEvent = charityEventRepository.findById(eventId);
        if(charityEvent.isEmpty()) {
            return new ResponseEntity<>("The event with id " + eventId + " doesn't exist!", HttpStatus.BAD_REQUEST);
        }

        CharityEvent event = charityEvent.get();
        if(event.getNeededMoney() == 0) {
            return new ResponseEntity<>("This charity event doesn't accept donations!", HttpStatus.BAD_REQUEST);
        }

        String cardNumberHash = SHA256.hash256(newDonationDto.getCardNumber());
        String cvvHash = SHA256.hash256(newDonationDto.getCvv().toString());
        Optional<CreditCard> creditCard = creditCardRepository.findByCardNumber(cardNumberHash);
        if(creditCard.isEmpty()) {
            return new ResponseEntity<>("Credit card information is incorrect!", HttpStatus.BAD_REQUEST);
        }

        String firstNameHash = SHA256.hash256(newDonationDto.getOwnerFirstName());
        String lastNameHash = SHA256.hash256(newDonationDto.getOwnerLastName());
        if(!creditCard.get().getCvv().equals(cvvHash)
                || !Objects.equals(creditCard.get().getExpirationMonth(), newDonationDto.getExpirationMonth())
                || !Objects.equals(creditCard.get().getExpirationYear(), newDonationDto.getExpirationYear())
                || !creditCard.get().getOwnerFirstName().equals(firstNameHash)
                || !creditCard.get().getOwnerLastName().equals(lastNameHash)) {
            return new ResponseEntity<>("Credit card information is incorrect!", HttpStatus.BAD_REQUEST);
        }

        if(creditCard.get().getBalance() < newDonationDto.getAmount()) {
            return new ResponseEntity<>("We can't process this donation! Please check your credit card balance!",
                    HttpStatus.METHOD_NOT_ALLOWED);
        }

        User user = KeycloakHelper.getCurrentUser(userRepository);
        if(user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Integer bonusPoints = newDonationDto.getAmount() / 10;
        user.setPoints(user.getPoints() + bonusPoints);
        DonationId donationId = DonationId.builder()
                .userId(user.getId())
                .eventId(eventId)
                .date(LocalDate.now())
                .hour(LocalTime.now()).build();

        Donation donation = Donation.builder()
                .id(donationId)
                .amount(newDonationDto.getAmount())
                .event(event)
                .user(user)
                .build();
        donationRepository.save(donation);

        CreditCard currentCreditCard = creditCard.get();
        currentCreditCard.setBalance(currentCreditCard.getBalance() - newDonationDto.getAmount());
        creditCardRepository.save(currentCreditCard);

        event.setCollectedMoney(event.getCollectedMoney() + newDonationDto.getAmount());
        charityEventRepository.save(event);
        event.addDonation(user, newDonationDto.getAmount());

        return new ResponseEntity<>("Your donation was successfully processed! Thank you!", HttpStatus.OK);
    }

    public ResponseEntity<?> getDonationsForEvent(Long eventId) {

        Optional<CharityEvent> charityEvent = charityEventRepository.findById(eventId);
        if(charityEvent.isEmpty()) {
            return new ResponseEntity<>("The charity event with id " + eventId + " doesn't exist!", HttpStatus.NOT_FOUND);
        }

        List<Donation> donationList = charityEvent.get().getUserDonations();
        if(donationList.isEmpty()) {
            return new ResponseEntity<>("There are no donations for this event yet!", HttpStatus.OK);
        }

        List<DonationDto> donationDtoList = new ArrayList<>();
        for(Donation donation: donationList) {
            UserInfoDto userInfoDto = UserInfoDto.builder()
                    .id(donation.getUser().getId())
                    .email(donation.getUser().getEmail())
                    .username(donation.getUser().getUsername())
                    .build();

            DonationDto donationDto = DonationDto.builder()
                    .id(donation.getId())
                    .amount(donation.getAmount())
                    .user(userInfoDto).build();
            donationDtoList.add(donationDto);
        }

        return new ResponseEntity<>(donationDtoList, HttpStatus.OK);
    }
}
