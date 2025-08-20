package com.starwars.service;

import com.starwars.films.dto.FilmApiDto;
import com.starwars.shared.dto.api.*;
import com.starwars.shared.dto.PageResponseDto;
import com.starwars.films.dto.FilmResponseDto;
import com.starwars.shared.exception.ResourceNotFoundException;
import com.starwars.films.mapper.FilmMapper;
import com.starwars.films.service.FilmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FilmServiceUnitTest {

    @Mock
    private FilmMapper filmMapper;

    @InjectMocks
    private FilmService filmService;

    @BeforeEach
    public void setUp() {
        // Configuramos el baseUrl que se inyecta por @Value
        ReflectionTestUtils.setField(filmService, "baseUrl", "https://swapi.tech/api");
    }

    @Test
    public void listOrSearchFilms_WithTitleParameter_ShouldReturnSearchResults() {
        // Arrange
        String searchTitle = "Hope";
        int page = 1;
        int limit = 10;

        FilmApiDto filmApiDto = createFilmApiDto("1", "A New Hope", 4, "George Lucas");
        ApiDetailResult<FilmApiDto> detailResult = createApiDetailResult("1", filmApiDto);
        List<ApiDetailResult<FilmApiDto>> resultList = Arrays.asList(detailResult);
        ApiEntityResponse<List<ApiDetailResult<FilmApiDto>>> apiResponse = createApiEntityResponse(resultList);

        FilmResponseDto responseDto = createFilmResponseDto("1", "A New Hope", 4, "George Lucas");

        when(filmMapper.toResponseDtoFromDetail(any(ApiResult.class))).thenReturn(responseDto);

        // Simulamos el método fetchApiData del padre
        FilmService spyService = spy(filmService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<FilmResponseDto> result = spyService.listOrSearchFilms(searchTitle, page, limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("A New Hope", result.getContent().get(0).getTitle());
        assertEquals(4, result.getContent().get(0).getEpisodeId());
        assertEquals("George Lucas", result.getContent().get(0).getDirector());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
        assertFalse(result.isHasNext());
        assertFalse(result.isHasPrevious());

        verify(filmMapper).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test
    public void listOrSearchFilms_WithTitleParameter_NoResults_ShouldReturnEmptyPage() {
        // Arrange
        String searchTitle = "NonExistent";
        int page = 1;
        int limit = 10;

        ApiEntityResponse<List<ApiDetailResult<FilmApiDto>>> apiResponse = createApiEntityResponse(Collections.emptyList());

        FilmService spyService = spy(filmService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<FilmResponseDto> result = spyService.listOrSearchFilms(searchTitle, page, limit);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    public void listOrSearchFilms_WithoutTitleParameter_ShouldReturnAllFilms() {
        // Arrange
        int page = 1;
        int limit = 10;

        FilmApiDto filmApiDto1 = createFilmApiDto("1", "A New Hope", 4, "George Lucas");
        FilmApiDto filmApiDto2 = createFilmApiDto("2", "The Empire Strikes Back", 5, "Irvin Kershner");
        
        ApiDetailResult<FilmApiDto> detailResult1 = createApiDetailResult("1", filmApiDto1);
        ApiDetailResult<FilmApiDto> detailResult2 = createApiDetailResult("2", filmApiDto2);
        List<ApiDetailResult<FilmApiDto>> resultList = Arrays.asList(detailResult1, detailResult2);
        ApiEntityResponse<List<ApiDetailResult<FilmApiDto>>> apiResponse = createApiEntityResponse(resultList);

        FilmResponseDto responseDto1 = createFilmResponseDto("1", "A New Hope", 4, "George Lucas");
        FilmResponseDto responseDto2 = createFilmResponseDto("2", "The Empire Strikes Back", 5, "Irvin Kershner");

        when(filmMapper.toResponseDtoFromDetail(any(ApiResult.class)))
                .thenReturn(responseDto1)
                .thenReturn(responseDto2);

        // Simulamos el método fetchApiData del padre
        FilmService spyService = spy(filmService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<FilmResponseDto> result = spyService.listOrSearchFilms(null, page, limit);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("A New Hope", result.getContent().get(0).getTitle());
        assertEquals("The Empire Strikes Back", result.getContent().get(1).getTitle());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        verify(filmMapper, times(2)).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test
    public void listOrSearchFilms_WithEmptyTitle_ShouldTreatAsNull() {
        // Arrange
        String searchTitle = "   "; // Solo espacios en blanco
        int page = 1;
        int limit = 10;

        FilmApiDto filmApiDto = createFilmApiDto("1", "A New Hope", 4, "George Lucas");
        ApiDetailResult<FilmApiDto> detailResult = createApiDetailResult("1", filmApiDto);
        List<ApiDetailResult<FilmApiDto>> resultList = Arrays.asList(detailResult);
        ApiEntityResponse<List<ApiDetailResult<FilmApiDto>>> apiResponse = createApiEntityResponse(resultList);

        FilmResponseDto responseDto = createFilmResponseDto("1", "A New Hope", 4, "George Lucas");

        when(filmMapper.toResponseDtoFromDetail(any(ApiResult.class))).thenReturn(responseDto);

        // Simulamos el método fetchApiData del padre
        FilmService spyService = spy(filmService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<FilmResponseDto> result = spyService.listOrSearchFilms(searchTitle, page, limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("A New Hope", result.getContent().get(0).getTitle());

        verify(filmMapper).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test
    public void getFilmById_ValidId_ShouldReturnFilm() {
        // Arrange
        String filmId = "1";
        FilmApiDto filmApiDto = createFilmApiDto("1", "A New Hope", 4, "George Lucas");
        ApiDetailResult<FilmApiDto> detailResult = createApiDetailResult("1", filmApiDto);
        ApiEntityResponse<ApiDetailResult<FilmApiDto>> apiResponse = createApiEntityResponse(detailResult);

        FilmResponseDto responseDto = createFilmResponseDto("1", "A New Hope", 4, "George Lucas");

        when(filmMapper.toResponseDtoFromDetail(any(ApiResult.class))).thenReturn(responseDto);

        // Simulamos el método fetchApiData del padre
        FilmService spyService = spy(filmService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        FilmResponseDto result = spyService.getFilmById(filmId);

        // Assert
        assertNotNull(result);
        assertEquals("A New Hope", result.getTitle());
        assertEquals(4, result.getEpisodeId());
        assertEquals("George Lucas", result.getDirector());

        verify(filmMapper).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test
    public void getFilmById_InvalidId_ShouldThrowResourceNotFoundException() {
        // Arrange
        String filmId = "999";
        ApiEntityResponse<ApiDetailResult<FilmApiDto>> apiResponse = createApiEntityResponse(null);

        // Simulamos el método fetchApiData del padre
        FilmService spyService = spy(filmService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act & Assert
        try {
            spyService.getFilmById(filmId);
            fail("Expected ResourceNotFoundException to be thrown");
        } catch (ResourceNotFoundException e) {
            assertEquals("Film no encontrado con id : '999'", e.getMessage());
        }
    }

    @Test
    public void getFilmById_ApiError_ShouldThrowResourceNotFoundException() {
        // Arrange
        String filmId = "999";

        // Simulamos el método fetchApiData del padre para que lance una excepción
        FilmService spyService = spy(filmService);
        doThrow(new RuntimeException("API Error")).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act & Assert
        try {
            spyService.getFilmById(filmId);
            fail("Expected ResourceNotFoundException to be thrown");
        } catch (ResourceNotFoundException e) {
            assertEquals("Film no encontrado con id : '999'", e.getMessage());
        }
    }

    // Métodos de ayuda para crear objetos de test
    private FilmApiDto createFilmApiDto(String uid, String title, int episodeId, String director) {
        FilmApiDto dto = new FilmApiDto();
        dto.setTitle(title);
        dto.setEpisodeId(episodeId);
        dto.setDirector(director);
        dto.setProducer("Gary Kurtz, Rick McCallum");
        dto.setReleaseDate("1977-05-25");
        dto.setOpeningCrawl("It is a period of civil war...");
        return dto;
    }

    private FilmResponseDto createFilmResponseDto(String uid, String title, int episodeId, String director) {
        FilmResponseDto dto = new FilmResponseDto();
        dto.setTitle(title);
        dto.setEpisodeId(episodeId);
        dto.setDirector(director);
        dto.setProducer("Gary Kurtz, Rick McCallum");
        dto.setReleaseDate("1977-05-25");
        dto.setOpeningCrawl("It is a period of civil war...");
        return dto;
    }

    private <T> ApiDetailResult<T> createApiDetailResult(String uid, T properties) {
        ApiDetailResult<T> result = new ApiDetailResult<>();
        result.setUid(uid);
        result.setProperties(properties);
        return result;
    }

    private <T> ApiEntityResponse<T> createApiEntityResponse(T result) {
        ApiEntityResponse<T> response = new ApiEntityResponse<>();
        response.setResult(result);
        return response;
    }
}
