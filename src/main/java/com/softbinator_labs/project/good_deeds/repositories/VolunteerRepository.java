package com.softbinator_labs.project.good_deeds.repositories;

import com.softbinator_labs.project.good_deeds.models.Volunteer;
import com.softbinator_labs.project.good_deeds.models.VolunteerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    @Query("SELECT v FROM Volunteer v WHERE v.id = ?1")
    Optional<Volunteer> findById(VolunteerId id);
}
