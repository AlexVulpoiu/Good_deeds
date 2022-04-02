package com.softbinator_labs.project.good_deeds.repositories;

import com.softbinator_labs.project.good_deeds.models.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    Optional<Voucher> findById(Long id);
}
