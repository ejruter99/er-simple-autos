package com.galvanize.autos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutoRepository extends JpaRepository<Automobile, Long> {
    List<Automobile> findByColorContainsAndMakeContains(String color, String make);
    List<Automobile> findByColorContains(String color);
    List<Automobile> findByYear(int year);
    Optional<Automobile> findByVin(String vin);
}
