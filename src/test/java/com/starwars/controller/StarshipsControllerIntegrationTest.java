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
public class StarshipsControllerIntegrationTest {

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
    public void listStarships_WithoutNameFilter_ShouldReturnPaginatedResults() throws Exception {
        // Arrange - Mock de la respuesta de la API externa
        String mockApiResponse = "{\n" +
                "  \"message\": \"ok\",\n" +
                "  \"total_records\": 36,\n" +
                "  \"total_pages\": 4,\n" +
                "  \"previous\": null,\n" +
                "  \"next\": \"https://swapi.tech/api/starships?page=2&limit=10\",\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"uid\": \"2\",\n" +
                "      \"properties\": {\n" +
                "        \"name\": \"CR90 corvette\",\n" +
                "        \"model\": \"CR90 corvette\",\n" +
                "        \"manufacturer\": \"Corellian Engineering Corporation\",\n" +
                "        \"cost_in_credits\": \"3500000\",\n" +
                "        \"length\": \"150\",\n" +
                "        \"crew\": \"30-165\",\n" +
                "        \"passengers\": \"600\",\n" +
                "        \"starship_class\": \"corvette\",\n" +
                "        \"url\": \"https://swapi.tech/api/starships/2\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"uid\": \"3\",\n" +
                "      \"properties\": {\n" +
                "        \"name\": \"Star Destroyer\",\n" +
                "        \"model\": \"Imperial I-class Star Destroyer\",\n" +
                "        \"manufacturer\": \"Kuat Drive Yards\",\n" +
                "        \"cost_in_credits\": \"150000000\",\n" +
                "        \"length\": \"1,600\",\n" +
                "        \"crew\": \"47,060\",\n" +
                "        \"passengers\": \"n/a\",\n" +
                "        \"starship_class\": \"Star Destroyer\",\n" +
                "        \"url\": \"https://swapi.tech/api/starships/3\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        stubFor(WireMock.get(urlPathEqualTo("/api/starships"))
                .withQueryParam("page", equalTo("1"))
                .withQueryParam("limit", equalTo("10"))
                .withQueryParam("expanded", equalTo("true"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockApiResponse)));

        // Act & Assert
        mockMvc.perform(get("/starships")
                        .param("page", "1")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("CR90 corvette")))
                .andExpect(jsonPath("$.content[0].model", is("CR90 corvette")))
                .andExpect(jsonPath("$.content[1].name", is("Star Destroyer")))
                .andExpect(jsonPath("$.totalElements", is(36)))
                .andExpect(jsonPath("$.totalPages", is(4)))
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.size", is(2)));
    }

    @Test
    public void listStarships_WithNameFilter_ShouldReturnFilteredResults() throws Exception {
        // Arrange - Mock de la respuesta de la API externa para búsqueda
        String mockApiResponse = "{\n" +
                "  \"message\": \"ok\",\n" +
                "  \"result\": [\n" +
                "    {\n" +
                "      \"uid\": \"10\",\n" +
                "      \"properties\": {\n" +
                "        \"name\": \"Millennium Falcon\",\n" +
                "        \"model\": \"YT-1300 light freighter\",\n" +
                "        \"manufacturer\": \"Corellian Engineering Corporation\",\n" +
                "        \"cost_in_credits\": \"100000\",\n" +
                "        \"length\": \"34.37\",\n" +
                "        \"crew\": \"4\",\n" +
                "        \"passengers\": \"6\",\n" +
                "        \"starship_class\": \"Light freighter\",\n" +
                "        \"url\": \"https://swapi.tech/api/starships/10\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        stubFor(WireMock.get(urlPathEqualTo("/api/starships"))
                .withQueryParam("name", equalTo("Falcon"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockApiResponse)));

        // Act & Assert
        mockMvc.perform(get("/starships")
                        .param("name", "Falcon")
                        .param("page", "1")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Millennium Falcon")))
                .andExpect(jsonPath("$.content[0].model", is("YT-1300 light freighter")));
    }

    @Test
    public void getStarshipById_ValidId_ShouldReturnStarship() throws Exception {
        // Arrange - Mock de la respuesta de la API externa para ID específico
        String mockApiResponse = "{\n" +
                "  \"message\": \"ok\",\n" +
                "  \"result\": {\n" +
                "    \"uid\": \"10\",\n" +
                "    \"properties\": {\n" +
                "      \"name\": \"Millennium Falcon\",\n" +
                "      \"model\": \"YT-1300 light freighter\",\n" +
                "      \"manufacturer\": \"Corellian Engineering Corporation\",\n" +
                "      \"cost_in_credits\": \"100000\",\n" +
                "      \"length\": \"34.37\",\n" +
                "      \"crew\": \"4\",\n" +
                "      \"passengers\": \"6\",\n" +
                "      \"starship_class\": \"Light freighter\",\n" +
                "      \"url\": \"https://swapi.tech/api/starships/10\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        stubFor(WireMock.get(urlPathEqualTo("/api/starships/10"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockApiResponse)));

        // Act & Assert
        mockMvc.perform(get("/starships/10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("10")))
                .andExpect(jsonPath("$.name", is("Millennium Falcon")))
                .andExpect(jsonPath("$.model", is("YT-1300 light freighter")))
                .andExpect(jsonPath("$.manufacturer", is("Corellian Engineering Corporation")))
                .andExpect(jsonPath("$.costInCredits", is("100000")))
                .andExpect(jsonPath("$.length", is("34.37")))
                .andExpect(jsonPath("$.crew", is("4")));
    }

    @Test
    public void getStarshipById_InvalidId_ShouldReturnNotFound() throws Exception {
        // Arrange - Mock de respuesta 404
        stubFor(WireMock.get(urlPathEqualTo("/api/starships/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Not found\"}")));

        // Act & Assert
        mockMvc.perform(get("/starships/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void listStarships_WithInvalidPageParameter_ShouldReturn400() throws Exception {
        // Act & Assert - Página inválida (menor a 1)
        mockMvc.perform(get("/starships")
                        .param("page", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void listStarships_WithInvalidLimitParameter_ShouldReturn400() throws Exception {
        // Act & Assert - Límite inválido (menor a 1)
        mockMvc.perform(get("/starships")
                        .param("page", "1")
                        .param("limit", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}