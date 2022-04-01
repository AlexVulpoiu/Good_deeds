package com.softbinator_labs.project.good_deeds.repositories;

import com.softbinator_labs.project.good_deeds.models.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

}
