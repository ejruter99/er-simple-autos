package com.galvanize.autos;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutosService {

    AutoRepository autoRepository;

    public AutosService(AutoRepository autoRepository) {
        this.autoRepository = autoRepository;
    }

    public AutosList getAutos() {

        return new AutosList(autoRepository.findAll());
    }

    public AutosList getAutos(String color, String make) {
        List<Automobile> automobiles = autoRepository.findByColorContainsAndMakeContains(color, make);
        if (!automobiles.isEmpty()) {
            return new AutosList(automobiles);
        }
        return null;
    }

    public AutosList getAutos(String color) {
        List<Automobile> automobiles = autoRepository.findByColorContains(color);
        if (!automobiles.isEmpty()) {
            return new AutosList(automobiles);
        }
        return null;
    }

    public AutosList getAutos(int year) {
        List<Automobile> automobiles = autoRepository.findByYear(year);
        if (!automobiles.isEmpty()) {
            return new AutosList(automobiles);
        }
        return null;
    }

    public Automobile addAuto(Automobile auto) {
        return autoRepository.save(auto);
    }

    public Automobile getAuto(String vin) {
        return autoRepository.findByVin(vin).orElse(null);
    }

    public Automobile updateAuto(String vin, String color, String owner) {
        Optional<Automobile> automobile = autoRepository.findByVin(vin);
        if (automobile.isPresent()) {
            automobile.get().setColor(color);
            automobile.get().setOwner(owner);
            return autoRepository.save(automobile.get());
        }
        return null;
    }

    public void deleteAuto(String vin) {
        Optional<Automobile> auto = autoRepository.findByVin(vin);
        if(auto.isPresent()) {
            autoRepository.delete(auto.get());
        } else {
            throw new AutoNotFoundException();
        }
    }
}
