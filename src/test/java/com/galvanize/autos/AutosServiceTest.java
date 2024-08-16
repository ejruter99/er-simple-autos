package com.galvanize.autos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutosServiceTest {

    private AutosService autosService;

    @Mock
    AutoRepository autoRepository;

    @BeforeEach
    void setUo() {
        autosService = new AutosService(autoRepository);
    }

    @Test
    void getAutos() {
        Automobile auto = new Automobile(1990, "Ford", "Mustang", "ABCD");
        when(autoRepository.findAll()).thenReturn(Arrays.asList(auto));
        AutosList autosList = autosService.getAutos();
        assertThat(autosList).isNotNull();
        assertThat(autosList.isEmpty()).isFalse();
    }

    @Test
    void getAutosSearchReturnsList() {
        Automobile auto = new Automobile(1990, "Ford", "Mustang", "ABCD");
        auto.setColor("RED");
        when(autoRepository.findByColorContainsAndMakeContains(anyString(), anyString()))
                .thenReturn(Arrays.asList(auto));
        AutosList autosList = autosService.getAutos("RED", "Ford");
        assertThat(autosList).isNotNull();
        assertThat(autosList.isEmpty()).isFalse();
    }

    @Test
    void getAutosSearchColorReturnsList() {
        Automobile auto = new Automobile(1990, "Ford", "Mustang", "ABCD");
        auto.setColor("RED");
        when(autoRepository.findByColorContains(anyString()))
                .thenReturn(Arrays.asList(auto));
        AutosList autosList = autosService.getAutos("RED");
        assertThat(autosList).isNotNull();
        assertThat(autosList.isEmpty()).isFalse();
    }

    @Test
    void getAutosSearchYearReturnsList() {
        Automobile auto = new Automobile(1990, "Ford", "Mustang", "ABCD");
        auto.setColor("RED");
        when(autoRepository.findByYear(anyInt()))
                .thenReturn(Arrays.asList(auto));
        AutosList autosList = autosService.getAutos(1990);
        assertThat(autosList).isNotNull();
        assertThat(autosList.isEmpty()).isFalse();
    }

    @Test
    void addAutoSuccessReturnsAuto() {
        Automobile auto = new Automobile(1990, "Mustang", "Ford", "ABCD");
        auto.setColor("RED");
        when(autoRepository.save(any(Automobile.class)))
                .thenReturn(auto);
        Automobile automobile = autosService.addAuto(auto);
        assertThat(automobile).isNotNull();
        assertThat(automobile.getMake()).isEqualTo("Ford");
    }

    @Test
    void getAutoWithVinReturnsAuto() {
        Automobile auto = new Automobile(1990, "Ford", "Mustang", "ABCD");
        auto.setColor("RED");
        when(autoRepository.findByVin(anyString()))
                .thenReturn(Optional.of(auto));
        Automobile automobile = autosService.getAuto(auto.getVin());
        assertThat(automobile).isNotNull();
        assertThat(automobile.getVin()).isEqualTo(auto.getVin());
    }

    @Test
    void updateAuto() {
        Automobile auto = new Automobile(1990, "Ford", "Mustang", "ABCD");
        auto.setColor("RED");
        when(autoRepository.findByVin(anyString()))
                .thenReturn(Optional.of(auto));
        when(autoRepository.save(any(Automobile.class)))
                .thenReturn(auto);
        Automobile automobile = autosService.updateAuto(auto.getVin(), "PURPLE", "Anyone");
        assertThat(automobile).isNotNull();
        assertThat(automobile.getVin()).isEqualTo(auto.getVin());
    }

    @Test
    void deleteAutoByVin() {
        Automobile auto = new Automobile(1990, "Ford", "Mustang", "ABCD");
        auto.setColor("RED");
        when(autoRepository.findByVin(anyString())).thenReturn(Optional.of(auto));

        autosService.deleteAuto(auto.getVin());

        verify(autoRepository).delete(any(Automobile.class));
    }

    @Test
    void deleteAutoByVinNotExost() {
        when(autoRepository.findByVin(anyString())).thenReturn(Optional.empty());

        assertThatExceptionOfType(AutoNotFoundException.class)
                .isThrownBy(() -> {
                    autosService.deleteAuto("NOT-EXIST-VIN");
                });

    }


}