package com.softbinator_labs.project.good_deeds.repositories;

import com.softbinator_labs.project.good_deeds.models.CharityEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharityEventRepository extends JpaRepository<CharityEvent, Long> {

    Optional<CharityEvent> findByTitle(String title);
}
