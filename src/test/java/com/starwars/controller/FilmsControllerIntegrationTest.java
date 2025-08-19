package com.starwars.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.starwars.StarWarsApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StarWarsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@TestPropertySource(properties = {
        "swapi.api.base-url=http://localhost:9999/api"
})
public class FilmsControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private WireMockServer wireMockServer;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Configurar WireMock para simular la API externa
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(9999));
        wireMockServer.start();
        WireMock.configureFor("localhost", 9999);
    }

    @After
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void listFilms_WithoutTitleFilter_ShouldReturnPaginatedResults() throws Exception {
        // Arrange - Mock de la respuesta de la API externa para ID específico usando ApiEntityResponse
        String mockApiResponse = "{\n" +
                "  \"message\": \"ok\",\n" +
                "  \"result\": {\n" +
                "    \"uid\": \"1\",\n" +
                "    \"properties\": {\n" +
                "      \"title\": \"A New Hope\",\n" +
                "      \"episode_id\": 4,\n" +
                "      \"opening_crawl\": \"It is a period of civil war...\",\n" +
                "      \"director\": \"George Lucas\",\n" +
                "      \"producer\": \"Gary Kurtz, Rick McCallum\",\n" +
                "      \"release_date\": \"1977-05-25\",\n" +
                "      \"url\": \"https://swapi.tech/api/films/1\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        // Configurar WireMock para responder a la llamada por ID
        stubFor(WireMock.get(urlPathEqualTo("/api/films/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockApiResponse)));

        // Act & Assert
        mockMvc.perform(get("/films/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.title", is("A New Hope")))
                .andExpect(jsonPath("$.episodeId", is(4)))
                .andExpect(jsonPath("$.director", is("George Lucas")))
                .andExpect(jsonPath("$.producer", is("Gary Kurtz, Rick McCallum")))
                .andExpect(jsonPath("$.releaseDate", is("1977-05-25")));
    }

    @Test
    public void listFilms_WithTitleFilter_ShouldReturnFilteredResults() throws Exception {
        // Arrange - Mock de la respuesta de la API externa para búsqueda usando ApiEntityResponse
        String mockApiResponse = "{\n" +
                "  \"message\": \"ok\",\n" +
                "  \"result\": [\n" +
                "    {\n" +
                "      \"uid\": \"1\",\n" +
                "      \"properties\": {\n" +
                "        \"title\": \"A New Hope\",\n" +
                "        \"episode_id\": 4,\n" +
                "        \"opening_crawl\": \"It is a period of civil war...\",\n" +
                "        \"director\": \"George Lucas\",\n" +
                "        \"producer\": \"Gary Kurtz, Rick McCallum\",\n" +
                "        \"release_date\": \"1977-05-25\",\n" +
                "        \"url\": \"https://swapi.tech/api/films/1\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // Configurar WireMock para responder a la búsqueda por título
        stubFor(WireMock.get(urlPathEqualTo("/api/films"))
                .withQueryParam("title", equalTo("Hope"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockApiResponse)));

        // Act & Assert
        mockMvc.perform(get("/films")
                        .param("title", "Hope")
                        .param("page", "1")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("A New Hope")))
                .andExpect(jsonPath("$.content[0].episodeId", is(4)))
                .andExpect(jsonPath("$.content[0].director", is("George Lucas")));
    }

    @Test
    public void getFilmById_ValidId_ShouldReturnFilm() throws Exception {
        // Arrange - Mock de la respuesta de la API externa para ID específico usando ApiEntityResponse
        String mockApiResponse = "{\n" +
                "  \"message\": \"ok\",\n" +
                "  \"result\": {\n" +
                "    \"uid\": \"1\",\n" +
                "    \"properties\": {\n" +
                "      \"title\": \"A New Hope\",\n" +
                "      \"episode_id\": 4,\n" +
                "      \"opening_crawl\": \"It is a period of civil war...\",\n" +
                "      \"director\": \"George Lucas\",\n" +
                "      \"producer\": \"Gary Kurtz, Rick McCallum\",\n" +
                "      \"release_date\": \"1977-05-25\",\n" +
                "      \"url\": \"https://swapi.tech/api/films/1\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        // Configurar WireMock para responder a la llamada por ID
        stubFor(WireMock.get(urlPathEqualTo("/api/films/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockApiResponse)));

        // Act & Assert
        mockMvc.perform(get("/films/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))  // Agregué validación del ID
                .andExpect(jsonPath("$.title", is("A New Hope")))
                .andExpect(jsonPath("$.episodeId", is(4)))
                .andExpect(jsonPath("$.director", is("George Lucas")))
                .andExpect(jsonPath("$.producer", is("Gary Kurtz, Rick McCallum")))
                .andExpect(jsonPath("$.releaseDate", is("1977-05-25")));
    }

    @Test
    public void getFilmById_InvalidId_ShouldReturnNotFound() throws Exception {
        // Arrange - Mock de error 404 o respuesta sin result
        String mockApiResponse = "{\n" +
                "  \"message\": \"not found\"\n" +
                "}";

        // Configurar WireMock para responder con error 404
        stubFor(WireMock.get(urlPathEqualTo("/api/films/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockApiResponse)));

        // Act & Assert
        mockMvc.perform(get("/films/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Film")));
    }

    @Test
    public void listFilms_WithInvalidPagination_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/films")
                        .param("page", "0")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void listFilms_WithLargeLimit_ShouldReturnBadRequest() throws Exception {
        // Act & Assert - Límite inválido (mayor a 100)
        mockMvc.perform(get("/films")
                        .param("page", "1")
                        .param("limit", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void listFilms_WithTitleFilter_NoResults_ShouldReturnEmptyPage() throws Exception {
        // Arrange - Mock de respuesta sin resultados para búsqueda
        String mockEmptyResponse = "{\n" +
                "  \"message\": \"ok\",\n" +
                "  \"result\": []\n" +
                "}";

        stubFor(WireMock.get(urlPathEqualTo("/api/films"))
                .withQueryParam("title", equalTo("NonExistent"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockEmptyResponse)));

        // Act & Assert
        mockMvc.perform(get("/films")
                        .param("title", "NonExistent")
                        .param("page", "1")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.totalPages", is(0)));
    }

    @Test
    public void getFilmById_ApiError_ShouldReturn404() throws Exception {
        // Arrange - Mock de error de servidor
        stubFor(WireMock.get(urlPathEqualTo("/api/films/error"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Internal server error\"}")));

        // Act & Assert
        mockMvc.perform(get("/films/error"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}