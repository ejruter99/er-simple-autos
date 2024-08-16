package com.galvanize.autos;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AutosController.class)
public class AutosControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AutosService autosService;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void getAutosReturnsAllAutos() throws Exception {
        List<Automobile> automobiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            automobiles.add(new Automobile(1990+i, "Ford", "Mustang", "ABCD"+i));
        }
        when(autosService.getAutos()).thenReturn(new AutosList(automobiles));
        mockMvc.perform(get("/api/autos"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.automobiles", hasSize(5)));
    }

    @Test
    void getAutosReturnsNoContent() throws Exception {
        when(autosService.getAutos()).thenReturn(new AutosList());
        mockMvc.perform(get("/api/autos"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void getAutosSearchParamsColorAndMakeExistsReturnsAutosList() throws Exception{
        List<Automobile> automobiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            automobiles.add(new Automobile(1990+i, "Ford", "Mustang", "ABCD"+i));
        }
        when(autosService.getAutos(anyString(), anyString())).thenReturn(new AutosList(automobiles));
        mockMvc.perform(get("/api/autos?color=RED&make=Ford"))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.automobiles", hasSize(5))));
    }

    @Test
    void getAutosSearchParamsColorExistsReturnsAutosList() throws Exception{
        List<Automobile> automobiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            automobiles.add(new Automobile(1990+i, "Ford", "Mustang", "ABCD"+i));
        }
        when(autosService.getAutos(anyString())).thenReturn(new AutosList(automobiles));
        mockMvc.perform(get("/api/autos?color=RED"))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.automobiles", hasSize(5))));
    }

    @Test
    void getAutosSearchParamsYearExistsReturnsAutosList() throws Exception{
        List<Automobile> automobiles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            automobiles.add(new Automobile(1990+i, "Ford", "Mustang", "ABCD"+i));
        }
        when(autosService.getAutos(anyInt())).thenReturn(new AutosList(automobiles));
        mockMvc.perform(get("/api/autos?year=1990"))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.automobiles", hasSize(5))));
    }

    @Test
    void getAutosUsingVinReturnsAuto() throws Exception {
        Automobile auto = new Automobile(1990, "Ford", "Mustang", "ABCD");
        when(autosService.getAuto(anyString())).thenReturn(auto);
        mockMvc.perform(get("/api/autos/" + auto.getVin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("vin").value(auto.getVin()));
    }

    @Test
    void getAutosUsingVinReturnsFailedAuto() throws Exception {
        Automobile auto = new Automobile(1990, "Ford", "Mustang", "ABCD");
        when(autosService.getAuto(anyString())).thenThrow(InvalidAutoException.class);
        mockMvc.perform(get("/api/autos/" + auto.getVin()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

//    @Test
//    void getAutosUsingVinReturnsFailAuto() throws Exception {
//        List<Automobile> automobiles = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            automobiles.add(new Automobile(1990+i, "Ford", "Mustang", "ABCD"+i));
//        }
//        when(autosService.getAutos(anyString())).thenReturn(new AutosList(automobiles));
//        mockMvc.perform(get("/api/autos/{vin}", "ABCD1"))
//                .andExpect(status().isOk())
//                .andExpect((jsonPath("$.automobiles", hasSize(5))));
//    }

    //CRUD possible api calls

    //GET calls that get all cars
    //GET: /api/autos 200 returns list of all autos (Done)
    //GET: /api/autos no autos in db returns 204 no content (Done)
    //GET: /api/autos?color=RED 200 returns all red cars (Done)
    //GET: /api/autos?year=1967 200 returns all cars made in 1998 (Done)
    //GET: /api/autos?make=Ford&model=Mustang 200 returns all cars that are mustangs made by ford (Done)
    //DONE = True

    //GET calls that use a vin
    //GET: /api/autos{vin} 200 returns the auto with the requested vin (Done)
    //GET: /api/autos{vin} 204 returns vehicle not found (Done)
    //DONE = True


    @Test
    void addAutoValidReturnsAuto() throws Exception {
        Automobile automobile = new Automobile(1967, "Mustang", "Ford", "ABCD6");
        when(autosService.addAuto(any(Automobile.class))).thenReturn(automobile);
        mockMvc.perform(post("/api/autos").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(automobile)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("make").value("Ford"));
    }

    @Test
    void addAutoBadReturnsFail() throws Exception {
        when(autosService.addAuto(any(Automobile.class))).thenThrow(InvalidAutoException.class);
        String json = "{ \"year\": \"1967\", \"make\": \"Ford\", \"model\": \"Mustang\", \"color\": \"null, \"owner\": \"null, \"vin\": \"ABCD7\"}";
        mockMvc.perform(post("/api/autos").contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }



    //POST: /api/autos returns 200 created auto (Done)
    //POST: /api/autos returns 400 failed to create auto (Done)
    //DONE = True

    @Test
    void updateAutoWithObjectReturnsAuto() throws Exception {
        Automobile automobile = new Automobile(1967, "Mustang", "Ford", "ABCD6");
        when(autosService.updateAuto(anyString(), anyString(), anyString())).thenReturn(automobile);
        mockMvc.perform(patch("/api/autos/"+automobile.getVin())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"color\":\"RED\", \"owner\":\"Eli\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("color").value("RED"))
                .andExpect(jsonPath("owner").value("Eli"));
    }

    @Test
    void updateAutoWithObjectReturnsBadRequest() throws Exception {
        Automobile automobile = new Automobile(1967, "Mustang", "Ford", "ABCD6");
        when(autosService.updateAuto(anyString(), anyString(), anyString())).thenThrow(InvalidAutoException.class);
        mockMvc.perform(patch("/api/autos/"+automobile.getVin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"color\":\"RED\", \"owner\":\"Eli\"}"))
                .andExpect(status().isBadRequest());
    }



    //PATCH: /api/autos{vin} 200 returns the successfully updated auto (Done)
    //PATCH: /api/autos{vin} 204 returns vehicle not found
    //PATCH: /api/autos{vin} 400 returns bad request (Done)
    //DONE = True

    @Test
    void deleteAutoWithVinExistsReturns202() throws Exception {
        mockMvc.perform(delete("/api/autos/ABCD6"))
                .andExpect(status().isAccepted());
        verify(autosService).deleteAuto(anyString());
    }

    @Test
    void deleteAutoWithVinAutoNotFound() throws Exception {
        doThrow(new AutoNotFoundException()).when(autosService).deleteAuto(anyString());
        mockMvc.perform(delete("/api/autos/ABCD6"))
                .andExpect(status().isNoContent());
    }

    //DELETE: /api/autos{vin} returns 202 delete accepted (Done)
    //DELETE: /api/autos/{vin{ returns 204 auto not found (Done)
    //DONE = True

}
