package com.galvanize.autos;

import org.aspectj.lang.annotation.Before;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(locations= "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AutosApiApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;
    RestTemplate patchRestTemplate;

    @Autowired
    AutoRepository autoRepository;

    Random r = new Random();
    List<Automobile> testAutos;
    @BeforeEach
    void setup(){
        this.testAutos = new ArrayList<>();
        String[] colors = {"RED", "BLUE", "GREEN", "ORANGE", "YELLOW", "BLACK", "BROWN", "ROOT BEER", "MAGENTA", "AMBER"};
        for (int i = 0; i < 50; i++) {
            Automobile auto;
            if (i % 3 == 0) {
                auto = new Automobile(1967, "Ford", "Mustang", "AABBCC" + (i * 13));
            } else if (i % 2 == 0) {
                auto = new Automobile(2000, "Dodge", "Viper", "VVBBXX" + (i * 12));
            } else {
                auto = new Automobile(2020, "Audi", "Quatro", "QQZZAA" + (i * 12));
            }
            auto.setColor(colors[r.nextInt(colors.length)]);
            this.testAutos.add(auto);
        }
        autoRepository.saveAll(this.testAutos);
    }

    @AfterEach
    void tearDown() {
        autoRepository.deleteAll();
    }

	@Test
	void contextLoads() {
	}

    @Test
    void getAutosExistsReturnsAutosList() {
        ResponseEntity<AutosList> response = restTemplate.getForEntity("/api/autos", AutosList.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isEmpty()).isFalse();
        for (Automobile auto : response.getBody().getAutomobiles()) {
            System.out.println(auto);
        }
    }

    @Test
    void getAutosByVinExistsReturnsAutosList() {
        String vin = testAutos.get(12).getVin();
        ResponseEntity<Automobile> response = restTemplate.getForEntity("/api/autos/" + vin, Automobile.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isEmpty()).isFalse();
        System.out.println(response.getBody());
    }

    @Before("updateAutosByVin")
    public void updateSetUp() {
        this.patchRestTemplate = restTemplate.getRestTemplate();
        HttpClient httpClient = HttpClient.newBuilder().build();
        this.patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

//    @Test
//    void updateAutosByVin() throws JSONException {
//        String vin = testAutos.get(12).getVin();
//        String uri = "/api/autos/" + vin;
//        JSONObject updateBody = new JSONObject();
//        updateBody.put("Yellow", "Eli");
//        ResponseEntity response = patchRestTemplate.exchange(uri, HttpMethod.PATCH, getPostRequestHeaders(updateBody.toString()), Automobile.class);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(MediaType.APPLICATION_JSON_UTF8, response.getHeaders().getContentType());
//    }

    @Test
    void getAutosByVinExistsReturnsNotFound() {
        String vin = "TestBadVin";
        ResponseEntity<Automobile> response = restTemplate.getForEntity("/api/autos/" + vin, Automobile.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void getAutosSearchReturnsAutosList() {
        int sequence = r.nextInt(50);
        String color = testAutos.get(sequence).getColor();
        String make = testAutos.get(sequence).getMake();
        ResponseEntity<AutosList> response = restTemplate.getForEntity(String.format("/api/autos?color=%s&make=%s", color, make), AutosList.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isEmpty()).isFalse();
        assertThat(response.getBody().getAutomobiles().size()).isGreaterThanOrEqualTo(1);
        for (Automobile auto : response.getBody().getAutomobiles()) {
            System.out.println(auto);
        }
    }

    @Test
    void addAuto_returnsNewAutoDetails() {
        Automobile automobile = new Automobile(1995, "Ford", "Windstar", "ABC123XX");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<Automobile> request = new HttpEntity<>(automobile, headers);

        ResponseEntity<Automobile> response = restTemplate.postForEntity("/api/autos", request, Automobile.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getVin()).isEqualTo(automobile.getVin());
    }

//    public HttpEntity getPostRequestHeaders(String jsonPostBody) {
//        List acceptTypes = new ArrayList();
//        acceptTypes.add(MediaType.APPLICATION_JSON_UTF8);
//
//        HttpHeaders reqHeaders = new HttpHeaders();
//        reqHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
//        reqHeaders.setAccept(acceptTypes);
//
//        return new HttpEntity(jsonPostBody, reqHeaders);
//    }

}
