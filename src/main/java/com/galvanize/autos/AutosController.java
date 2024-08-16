package com.galvanize.autos;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
public class AutosController {

    AutosService autosService;

    public AutosController(AutosService autosService) {
        this.autosService = autosService;
    }

    @GetMapping("/api/autos/{vin}")
    public Automobile getAutosWithVin(@PathVariable String vin) {
        return autosService.getAuto(vin);
    }

    @GetMapping("/api/autos")
    public ResponseEntity<AutosList> getAutos(@RequestParam(required = false) String color,
                                              @RequestParam(required = false) String make,
                                              @RequestParam(required = false) Integer year) {
        AutosList autosList;
        if (color == null && make == null && year == null) {
            autosList = autosService.getAutos();
        } else if (color != null && make == null & year == null) {
            autosList = autosService.getAutos(color);
        } else if (color == null && make == null & year != null){
            autosList = autosService.getAutos(year);
        } else {
            autosList = autosService.getAutos(color, make);
        }
        return autosList.isEmpty() ? ResponseEntity.noContent().build() :
                ResponseEntity.ok(autosList);
    }

    @PostMapping("/api/autos")
    public Automobile addAuto(@RequestBody Automobile auto){
        return autosService.addAuto(auto);
    }

    @PatchMapping("/api/autos/{vin}")
    public Automobile updateAuto(@PathVariable String vin,
                                 @RequestBody UpdateOwnerRequest update){
        Automobile auto = autosService.updateAuto(vin, update.getColor(), update.getOwner());
        auto.setColor(update.getColor());
        auto.setOwner(update.getOwner());
        return auto;
    }

    @DeleteMapping("/api/autos/{vin}")
    public ResponseEntity deleteAuto(@PathVariable String vin) {
        try {
            autosService.deleteAuto(vin);
        } catch (AutoNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.accepted().build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void invalidFormatExceptionHandler(InvalidAutoException e) {

    }




}
