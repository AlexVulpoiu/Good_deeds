package com.softbinator_labs.project.good_deeds.repositories;

import com.softbinator_labs.project.good_deeds.models.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

    Optional<Organisation> findByName(String name);
}
