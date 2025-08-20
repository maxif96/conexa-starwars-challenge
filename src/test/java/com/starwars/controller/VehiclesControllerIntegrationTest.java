package com.starwars.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.starwars.StarWarsApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = StarWarsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class VehiclesControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private WireMockServer wireMockServer;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Configurar WireMock para simular la API externa
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(9999));
        wireMockServer.start();
        WireMock.configureFor("localhost", 9999);
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void listVehicles_WithoutNameFilter_ShouldReturnPaginatedResults() throws Exception {
        // Arrange - Mock de la respuesta de la API externa
        String mockApiResponse = "{\n" +
                "  \"message\": \"ok\",\n" +
                "  \"total_records\": 39,\n" +
                "  \"total_pages\": 4,\n" +
                "  \"previous\": null,\n" +
                "  \"next\": \"https://swapi.tech/api/vehicles?page=2&limit=10\",\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"uid\": \"4\",\n" +
                "      \"properties\": {\n" +
                "        \"name\": \"Sand Crawler\",\n" +
                "        \"model\": \"Digger Crawler\",\n" +
                "        \"manufacturer\": \"Corellia Mining Corporation\",\n" +
                "        \"cost_in_credits\": \"150000\",\n" +
                "        \"length\": \"36.8\",\n" +
                "        \"crew\": \"30\",\n" +
                "        \"passengers\": \"30\",\n" +
                "        \"vehicle_class\": \"wheeled\",\n" +
                "        \"url\": \"https://swapi.tech/api/vehicles/4\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"uid\": \"6\",\n" +
                "      \"properties\": {\n" +
                "        \"name\": \"T-16 skyhopper\",\n" +
                "        \"model\": \"T-16 skyhopper\",\n" +
                "        \"manufacturer\": \"Incom Corporation\",\n" +
                "        \"cost_in_credits\": \"14500\",\n" +
                "        \"length\": \"10.4\",\n" +
                "        \"crew\": \"1\",\n" +
                "        \"passengers\": \"1\",\n" +
                "        \"vehicle_class\": \"repulsorcraft\",\n" +
                "        \"url\": \"https://swapi.tech/api/vehicles/6\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        stubFor(WireMock.get(urlPathEqualTo("/api/vehicles"))
                .withQueryParam("page", equalTo("1"))
                .withQueryParam("limit", equalTo("10"))
                .withQueryParam("expanded", equalTo("true"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockApiResponse)));

        // Act & Assert
        mockMvc.perform(get("/vehicles")
                        .param("page", "1")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("Sand Crawler")))
                .andExpect(jsonPath("$.content[0].model", is("Digger Crawler")))
                .andExpect(jsonPath("$.content[1].name", is("T-16 skyhopper")))
                .andExpect(jsonPath("$.totalElements", is(39)))
                .andExpect(jsonPath("$.totalPages", is(4)))
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.size", is(2)));
    }

    @Test
    public void listVehicles_WithNameFilter_ShouldReturnFilteredResults() throws Exception {
        // Arrange - Mock de la respuesta de la API externa para búsqueda
        String mockApiResponse = "{\n" +
                "  \"message\": \"ok\",\n" +
                "  \"result\": [\n" +
                "    {\n" +
                "      \"uid\": \"4\",\n" +
                "      \"properties\": {\n" +
                "        \"name\": \"Sand Crawler\",\n" +
                "        \"model\": \"Digger Crawler\",\n" +
                "        \"manufacturer\": \"Corellia Mining Corporation\",\n" +
                "        \"cost_in_credits\": \"150000\",\n" +
                "        \"length\": \"36.8\",\n" +
                "        \"crew\": \"30\",\n" +
                "        \"passengers\": \"30\",\n" +
                "        \"vehicle_class\": \"wheeled\",\n" +
                "        \"url\": \"https://swapi.tech/api/vehicles/4\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        stubFor(WireMock.get(urlPathEqualTo("/api/vehicles"))
                .withQueryParam("name", equalTo("Sand"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockApiResponse)));

        // Act & Assert
        mockMvc.perform(get("/vehicles")
                        .param("name", "Sand")
                        .param("page", "1")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Sand Crawler")))
                .andExpect(jsonPath("$.content[0].model", is("Digger Crawler")));
    }

    @Test
    public void getVehicleById_ValidId_ShouldReturnVehicle() throws Exception {
        // Arrange - Mock de la respuesta de la API externa para ID específico
        String mockApiResponse = "{\n" +
                "  \"message\": \"ok\",\n" +
                "  \"result\": {\n" +
                "    \"uid\": \"4\",\n" +
                "    \"properties\": {\n" +
                "      \"name\": \"Sand Crawler\",\n" +
                "      \"model\": \"Digger Crawler\",\n" +
                "      \"manufacturer\": \"Corellia Mining Corporation\",\n" +
                "      \"cost_in_credits\": \"150000\",\n" +
                "      \"length\": \"36.8\",\n" +
                "      \"crew\": \"30\",\n" +
                "      \"passengers\": \"30\",\n" +
                "      \"vehicle_class\": \"wheeled\",\n" +
                "      \"url\": \"https://swapi.tech/api/vehicles/4\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        stubFor(WireMock.get(urlPathEqualTo("/api/vehicles/4"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockApiResponse)));

        // Act & Assert
        mockMvc.perform(get("/vehicles/4"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("4")))
                .andExpect(jsonPath("$.name", is("Sand Crawler")))
                .andExpect(jsonPath("$.model", is("Digger Crawler")))
                .andExpect(jsonPath("$.manufacturer", is("Corellia Mining Corporation")))
                .andExpect(jsonPath("$.costInCredits", is("150000")))
                .andExpect(jsonPath("$.length", is("36.8")))
                .andExpect(jsonPath("$.crew", is("30")));
    }

    @Test
    public void getVehicleById_InvalidId_ShouldReturnNotFound() throws Exception {
        // Arrange - Mock de respuesta 404
        stubFor(WireMock.get(urlPathEqualTo("/api/vehicles/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Not found\"}")));

        // Act & Assert
        mockMvc.perform(get("/vehicles/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void listVehicles_WithInvalidPageParameter_ShouldReturn400() throws Exception {
        // Act & Assert - Página inválida (menor a 1)
        mockMvc.perform(get("/vehicles")
                        .param("page", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void listVehicles_WithInvalidLimitParameter_ShouldReturn400() throws Exception {
        // Act & Assert - Límite inválido (menor a 1)
        mockMvc.perform(get("/vehicles")
                        .param("page", "1")
                        .param("limit", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void listVehicles_WithEmptyNameFilter_ShouldReturnEmptyResults() throws Exception {
        // Arrange - Mock de respuesta sin resultados
        String mockEmptyResponse = "{\n" +
                "  \"message\": \"ok\",\n" +
                "  \"result\": []\n" +
                "}";

        stubFor(WireMock.get(urlPathEqualTo("/api/vehicles"))
                .withQueryParam("name", equalTo("NonExistent"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockEmptyResponse)));

        // Act & Assert
        mockMvc.perform(get("/vehicles")
                        .param("name", "NonExistent")
                        .param("page", "1")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.totalPages", is(0)));
    }
}