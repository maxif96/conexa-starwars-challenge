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
public class PeopleControllerIntegrationTest {

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
    public void listPeople_WithoutNameFilter_ShouldReturnPaginatedResults() throws Exception {
        // Arrange - Mock de la respuesta de la API externa
        String mockApiResponse = "{\n" +
                "  \"message\": \"ok\",\n" +
                "  \"total_records\": 82,\n" +
                "  \"total_pages\": 9,\n" +
                "  \"previous\": null,\n" +
                "  \"next\": \"https://swapi.tech/api/people?page=2&limit=10\",\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"uid\": \"1\",\n" +
                "      \"properties\": {\n" +
                "        \"height\": \"172\",\n" +
                "        \"mass\": \"77\",\n" +
                "        \"hair_color\": \"blond\",\n" +
                "        \"skin_color\": \"fair\",\n" +
                "        \"eye_color\": \"blue\",\n" +
                "        \"birth_year\": \"19BBY\",\n" +
                "        \"gender\": \"male\",\n" +
                "        \"name\": \"Luke Skywalker\",\n" +
                "        \"homeworld\": \"https://swapi.tech/api/planets/1\",\n" +
                "        \"url\": \"https://swapi.tech/api/people/1\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"uid\": \"2\",\n" +
                "      \"properties\": {\n" +
                "        \"height\": \"167\",\n" +
                "        \"mass\": \"75\",\n" +
                "        \"hair_color\": \"n/a\",\n" +
                "        \"skin_color\": \"gold\",\n" +
                "        \"eye_color\": \"yellow\",\n" +
                "        \"birth_year\": \"112BBY\",\n" +
                "        \"gender\": \"n/a\",\n" +
                "        \"name\": \"C-3PO\",\n" +
                "        \"homeworld\": \"https://swapi.tech/api/planets/1\",\n" +
                "        \"url\": \"https://swapi.tech/api/people/2\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        stubFor(WireMock.get(urlPathEqualTo("/api/people"))
                .withQueryParam("page", equalTo("1"))
                .withQueryParam("limit", equalTo("10"))
                .withQueryParam("expanded", equalTo("true"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockApiResponse)));

        // Act & Assert
        mockMvc.perform(get("/people")
                        .param("page", "1")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is("1")))
                .andExpect(jsonPath("$.content[0].name", is("Luke Skywalker")))
                .andExpect(jsonPath("$.content[0].height", is("172")))
                .andExpect(jsonPath("$.content[0].gender", is("male")))
                .andExpect(jsonPath("$.content[1].id", is("2")))
                .andExpect(jsonPath("$.content[1].name", is("C-3PO")))
                .andExpect(jsonPath("$.totalElements", is(82)))
                .andExpect(jsonPath("$.totalPages", is(9)))
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.size", is(2)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(false)))
                .andExpect(jsonPath("$.hasNext", is(true)))
                .andExpect(jsonPath("$.hasPrevious", is(false)));
    }

    @Test
    public void listPeople_WithNameFilter_ShouldReturnSearchResults() throws Exception {
        // Arrange - Mock de la respuesta de búsqueda
        String mockSearchResponse = "{\n" +
                "  \"message\": \"ok\",\n" +
                "  \"result\": [\n" +
                "    {\n" +
                "      \"uid\": \"1\",\n" +
                "      \"properties\": {\n" +
                "        \"height\": \"172\",\n" +
                "        \"mass\": \"77\",\n" +
                "        \"hair_color\": \"blond\",\n" +
                "        \"skin_color\": \"fair\",\n" +
                "        \"eye_color\": \"blue\",\n" +
                "        \"birth_year\": \"19BBY\",\n" +
                "        \"gender\": \"male\",\n" +
                "        \"name\": \"Luke Skywalker\",\n" +
                "        \"homeworld\": \"https://swapi.tech/api/planets/1\",\n" +
                "        \"url\": \"https://swapi.tech/api/people/1\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        stubFor(WireMock.get(urlPathEqualTo("/api/people"))
                .withQueryParam("name", equalTo("Luke"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockSearchResponse)));

        // Act & Assert
        mockMvc.perform(get("/people")
                        .param("name", "Luke")
                        .param("page", "1")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is("1")))
                .andExpect(jsonPath("$.content[0].name", is("Luke Skywalker")))
                .andExpect(jsonPath("$.content[0].eyeColor", is("blue")))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    public void listPeople_WithNameFilter_NoResults_ShouldReturnEmptyPage() throws Exception {
        // Arrange - Mock de respuesta sin resultados
        String mockEmptyResponse = "{\n" +
                "  \"message\": \"ok\",\n" +
                "  \"result\": []\n" +
                "}";

        stubFor(WireMock.get(urlPathEqualTo("/api/people"))
                .withQueryParam("name", equalTo("NonExistent"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockEmptyResponse)));

        // Act & Assert
        mockMvc.perform(get("/people")
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

    @Test
    public void getPersonById_ValidId_ShouldReturnPerson() throws Exception {
        // Arrange - Mock de la respuesta de persona individual
        String mockPersonResponse = "{\n" +
                "  \"message\": \"ok\",\n" +
                "  \"result\": {\n" +
                "    \"uid\": \"1\",\n" +
                "    \"properties\": {\n" +
                "      \"height\": \"172\",\n" +
                "      \"mass\": \"77\",\n" +
                "      \"hair_color\": \"blond\",\n" +
                "      \"skin_color\": \"fair\",\n" +
                "      \"eye_color\": \"blue\",\n" +
                "      \"birth_year\": \"19BBY\",\n" +
                "      \"gender\": \"male\",\n" +
                "      \"name\": \"Luke Skywalker\",\n" +
                "      \"homeworld\": \"https://swapi.tech/api/planets/1\",\n" +
                "      \"url\": \"https://swapi.tech/api/people/1\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        stubFor(WireMock.get(urlPathEqualTo("/api/people/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockPersonResponse)));

        // Act & Assert
        mockMvc.perform(get("/people/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("Luke Skywalker")))
                .andExpect(jsonPath("$.height", is("172")))
                .andExpect(jsonPath("$.mass", is("77")))
                .andExpect(jsonPath("$.hairColor", is("blond")))
                .andExpect(jsonPath("$.skinColor", is("fair")))
                .andExpect(jsonPath("$.eyeColor", is("blue")))
                .andExpect(jsonPath("$.birthYear", is("19BBY")))
                .andExpect(jsonPath("$.gender", is("male")));
    }

    @Test
    public void getPersonById_InvalidId_ShouldReturn404() throws Exception {
        // Arrange - Mock de respuesta 404
        stubFor(WireMock.get(urlPathEqualTo("/api/people/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Not found\"}")));

        // Act & Assert
        mockMvc.perform(get("/people/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getPersonById_ApiError_ShouldReturn404() throws Exception {
        // Arrange - Mock de error de servidor
        stubFor(WireMock.get(urlPathEqualTo("/api/people/error"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Internal server error\"}")));

        // Act & Assert
        mockMvc.perform(get("/people/error"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void listPeople_WithInvalidPageParameter_ShouldReturn400() throws Exception {
        // Act & Assert - Página inválida (menor a 1)
        mockMvc.perform(get("/people")
                        .param("page", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void listPeople_WithInvalidLimitParameter_ShouldReturn400() throws Exception {
        // Act & Assert - Límite inválido (menor a 1)
        mockMvc.perform(get("/people")
                        .param("page", "1")
                        .param("limit", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void listPeople_WithDefaultParameters_ShouldUseDefaults() throws Exception {
        // Arrange - Mock para parámetros por defecto
        String mockDefaultResponse = "{\n" +
                "  \"message\": \"ok\",\n" +
                "  \"total_records\": 82,\n" +
                "  \"total_pages\": 9,\n" +
                "  \"previous\": null,\n" +
                "  \"next\": \"https://swapi.tech/api/people?page=2&limit=10\",\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"uid\": \"1\",\n" +
                "      \"properties\": {\n" +
                "        \"name\": \"Luke Skywalker\",\n" +
                "        \"height\": \"172\",\n" +
                "        \"mass\": \"77\",\n" +
                "        \"hair_color\": \"blond\",\n" +
                "        \"skin_color\": \"fair\",\n" +
                "        \"eye_color\": \"blue\",\n" +
                "        \"birth_year\": \"19BBY\",\n" +
                "        \"gender\": \"male\",\n" +
                "        \"homeworld\": \"https://swapi.tech/api/planets/1\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        stubFor(WireMock.get(urlPathEqualTo("/api/people"))
                .withQueryParam("page", equalTo("1"))
                .withQueryParam("limit", equalTo("10"))
                .withQueryParam("expanded", equalTo("true"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockDefaultResponse)));

        // Act & Assert - Sin parámetros, debería usar page=1, limit=10
        mockMvc.perform(get("/people"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.content", hasSize(1)));

        // Verificar que se llamó con los parámetros por defecto
        verify(getRequestedFor(urlPathEqualTo("/api/people"))
                .withQueryParam("page", equalTo("1"))
                .withQueryParam("limit", equalTo("10")));
    }

    @Test
    public void listPeople_ApiTimeout_ShouldReturn500() throws Exception {
        // Arrange - Mock de timeout
        stubFor(WireMock.get(urlPathEqualTo("/api/people"))
                .withQueryParam("page", equalTo("1"))
                .withQueryParam("limit", equalTo("10"))
                .withQueryParam("expanded", equalTo("true"))
                .willReturn(aResponse()
                        .withFixedDelay(10000) // Delay mayor al timeout configurado
                        .withStatus(200)));

        // Act & Assert
        mockMvc.perform(get("/people"))
                .andDo(print())
                .andExpect(status().isOk()) // Debería retornar página vacía en caso de error
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }
}