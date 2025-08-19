package com.starwars.service;

import com.starwars.dto.api.*;
import com.starwars.dto.response.PageResponseDto;
import com.starwars.dto.response.StarshipResponseDto;
import com.starwars.exception.ResourceNotFoundException;
import com.starwars.mapper.StarshipMapper;
import com.starwars.service.StarshipService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StarshipServiceUnitTest {

    @Mock
    private StarshipMapper starshipMapper;

    @InjectMocks
    private StarshipService starshipService;

    @Before
    public void setUp() {
        // Configuramos el baseUrl que se inyecta por @Value
        ReflectionTestUtils.setField(starshipService, "baseUrl", "https://swapi.tech/api");
    }

    @Test
    public void listOrSearchStarships_WithNameParameter_ShouldReturnSearchResults() {
        // Arrange
        String searchName = "Falcon";
        int page = 1;
        int limit = 10;

        StarshipApiDto starshipApiDto = createStarshipApiDto("10", "Millennium Falcon", "4", "34.37");
        ApiDetailResult<StarshipApiDto> detailResult = createApiDetailResult("10", starshipApiDto);
        List<ApiDetailResult<StarshipApiDto>> resultList = Arrays.asList(detailResult);
        ApiEntityResponse<List<ApiDetailResult<StarshipApiDto>>> apiResponse = createApiEntityResponse(resultList);

        StarshipResponseDto responseDto = createStarshipResponseDto("10", "Millennium Falcon", "4", "34.37");

        when(starshipMapper.toResponseDtoFromDetail(any(ApiResult.class))).thenReturn(responseDto);

        // Simulamos el método fetchApiData del padre
        StarshipService spyService = spy(starshipService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<StarshipResponseDto> result = spyService.listOrSearchStarships(searchName, page, limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Millennium Falcon", result.getContent().get(0).getName());
        assertEquals("4", result.getContent().get(0).getCrew());
        assertEquals("34.37", result.getContent().get(0).getLength());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
        assertFalse(result.isHasNext());
        assertFalse(result.isHasPrevious());

        verify(starshipMapper).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test
    public void listOrSearchStarships_WithNameParameter_NoResults_ShouldReturnEmptyPage() {
        // Arrange
        String searchName = "NonExistent";
        int page = 1;
        int limit = 10;

        ApiEntityResponse<List<ApiDetailResult<StarshipApiDto>>> apiResponse = createApiEntityResponse(Collections.emptyList());

        StarshipService spyService = spy(starshipService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<StarshipResponseDto> result = spyService.listOrSearchStarships(searchName, page, limit);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    public void listOrSearchStarships_WithoutNameParameter_ShouldReturnPaginatedResults() {
        // Arrange
        int page = 1;
        int limit = 10;

        StarshipApiDto starshipApiDto1 = createStarshipApiDto("2", "CR90 corvette", "30-165", "150");
        StarshipApiDto starshipApiDto2 = createStarshipApiDto("3", "Star Destroyer", "47,060", "1,600");

        List<ApiDetailResult<StarshipApiDto>> resultList = Arrays.asList(
                createApiDetailResult("2", starshipApiDto1),
                createApiDetailResult("3", starshipApiDto2)
        );

        // CORRECCIÓN: Usar ApiPageResponse para casos sin filtro de nombre
        ApiPageResponse<StarshipApiDto> apiPageResponse = createApiPageResponse(resultList, 4, 36, "next_url", null);

        StarshipResponseDto responseDto1 = createStarshipResponseDto("2", "CR90 corvette", "30-165", "150");
        StarshipResponseDto responseDto2 = createStarshipResponseDto("3", "Star Destroyer", "47,060", "1,600");

        when(starshipMapper.toResponseDtoFromDetail(any(ApiResult.class)))
                .thenReturn(responseDto1)
                .thenReturn(responseDto2);

        StarshipService spyService = spy(starshipService);
        doReturn(apiPageResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<StarshipResponseDto> result = spyService.listOrSearchStarships(null, page, limit);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("CR90 corvette", result.getContent().get(0).getName());
        assertEquals("Star Destroyer", result.getContent().get(1).getName());
        assertEquals(36L, result.getTotalElements());
        assertEquals(4, result.getTotalPages());
        assertTrue(result.isFirst());
        assertFalse(result.isLast());
        assertTrue(result.isHasNext());
        assertFalse(result.isHasPrevious());

        verify(starshipMapper, times(2)).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test
    public void listOrSearchStarships_WithEmptyName_ShouldReturnPaginatedResults() {
        // Arrange
        String emptyName = "   "; // Solo espacios en blanco
        int page = 1;
        int limit = 10;

        StarshipApiDto starshipApiDto = createStarshipApiDto("10", "Millennium Falcon", "4", "34.37");
        List<ApiDetailResult<StarshipApiDto>> resultList = Arrays.asList(createApiDetailResult("10", starshipApiDto));

        ApiPageResponse<StarshipApiDto> apiPageResponse = createApiPageResponse(resultList, 1, 1, null, null);

        StarshipResponseDto responseDto = createStarshipResponseDto("10", "Millennium Falcon", "4", "34.37");
        when(starshipMapper.toResponseDtoFromDetail(any(ApiResult.class))).thenReturn(responseDto);

        StarshipService spyService = spy(starshipService);
        doReturn(apiPageResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<StarshipResponseDto> result = spyService.listOrSearchStarships(emptyName, page, limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Millennium Falcon", result.getContent().get(0).getName());
        verify(starshipMapper).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test
    public void listOrSearchStarships_WithNameParameter_NullApiResponse_ShouldReturnEmptyPage() {
        // Arrange
        String searchName = "Falcon";
        int page = 1;
        int limit = 10;

        StarshipService spyService = spy(starshipService);
        doReturn(null).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<StarshipResponseDto> result = spyService.listOrSearchStarships(searchName, page, limit);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    public void getStarshipById_ValidId_ShouldReturnStarship() {
        // Arrange
        String starshipId = "10";
        StarshipApiDto starshipApiDto = createStarshipApiDto("10", "Millennium Falcon", "4", "34.37");
        ApiDetailResult<StarshipApiDto> detailResult = createApiDetailResult("10", starshipApiDto);
        ApiEntityResponse<ApiDetailResult<StarshipApiDto>> apiResponse = createApiEntityResponse(detailResult);

        StarshipResponseDto responseDto = createStarshipResponseDto("10", "Millennium Falcon", "4", "34.37");

        when(starshipMapper.toResponseDtoFromDetail(any(ApiResult.class))).thenReturn(responseDto);

        StarshipService spyService = spy(starshipService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        StarshipResponseDto result = spyService.getStarshipById(starshipId);

        // Assert
        assertNotNull(result);
        assertEquals("10", result.getId());
        assertEquals("Millennium Falcon", result.getName());
        assertEquals("4", result.getCrew());
        assertEquals("34.37", result.getLength());

        verify(starshipMapper).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getStarshipById_NullApiResponse_ShouldThrowException() {
        // Arrange
        String starshipId = "999";

        StarshipService spyService = spy(starshipService);
        doReturn(null).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        spyService.getStarshipById(starshipId);

        // Assert - Se espera ResourceNotFoundException
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getStarshipById_NullResult_ShouldThrowException() {
        // Arrange
        String starshipId = "999";
        ApiEntityResponse<ApiDetailResult<StarshipApiDto>> apiResponse = createApiEntityResponse((ApiDetailResult<StarshipApiDto>) null);

        StarshipService spyService = spy(starshipService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        spyService.getStarshipById(starshipId);

        // Assert - Se espera ResourceNotFoundException
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getStarshipById_NullProperties_ShouldThrowException() {
        // Arrange
        String starshipId = "999";
        ApiDetailResult<StarshipApiDto> detailResult = new ApiDetailResult<>();
        detailResult.setUid("999");
        detailResult.setProperties(null);

        ApiEntityResponse<ApiDetailResult<StarshipApiDto>> apiResponse = createApiEntityResponse(detailResult);

        StarshipService spyService = spy(starshipService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        spyService.getStarshipById(starshipId);

        // Assert - Se espera ResourceNotFoundException
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getStarshipById_ApiThrowsException_ShouldThrowResourceNotFoundException() {
        // Arrange
        String starshipId = "1";

        StarshipService spyService = spy(starshipService);
        doThrow(new RuntimeException("API Error")).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        spyService.getStarshipById(starshipId);

        // Assert - Se espera ResourceNotFoundException
    }

    // Métodos auxiliares para crear objetos de prueba
    private StarshipApiDto createStarshipApiDto(String uid, String name, String crew, String length) {
        StarshipApiDto dto = new StarshipApiDto();
        ReflectionTestUtils.setField(dto, "name", name);
        ReflectionTestUtils.setField(dto, "crew", crew);
        ReflectionTestUtils.setField(dto, "length", length);
        ReflectionTestUtils.setField(dto, "model", "YT-1300 light freighter");
        ReflectionTestUtils.setField(dto, "manufacturer", "Corellian Engineering Corporation");
        ReflectionTestUtils.setField(dto, "costInCredits", "100000");
        ReflectionTestUtils.setField(dto, "passengers", "6");
        ReflectionTestUtils.setField(dto, "starshipClass", "Light freighter");
        return dto;
    }

    private ApiDetailResult<StarshipApiDto> createApiDetailResult(String uid, StarshipApiDto properties) {
        ApiDetailResult<StarshipApiDto> result = new ApiDetailResult<>();
        result.setUid(uid);
        result.setProperties(properties);
        return result;
    }

    private ApiEntityResponse<List<ApiDetailResult<StarshipApiDto>>> createApiEntityResponse(List<ApiDetailResult<StarshipApiDto>> result) {
        ApiEntityResponse<List<ApiDetailResult<StarshipApiDto>>> response = new ApiEntityResponse<>();
        response.setResult(result);
        return response;
    }

    private ApiEntityResponse<ApiDetailResult<StarshipApiDto>> createApiEntityResponse(ApiDetailResult<StarshipApiDto> result) {
        ApiEntityResponse<ApiDetailResult<StarshipApiDto>> response = new ApiEntityResponse<>();
        response.setResult(result);
        return response;
    }

    private ApiPageResponse<StarshipApiDto> createApiPageResponse(List<ApiDetailResult<StarshipApiDto>> results,
                                                                  Integer totalPages, Integer totalRecords,
                                                                  String next, String previous) {
        ApiPageResponse<StarshipApiDto> response = new ApiPageResponse<>();
        response.setResults(results);
        response.setTotalPages(totalPages);
        response.setTotalRecords(totalRecords);
        response.setNext(next);
        response.setPrevious(previous);
        return response;
    }

    private StarshipResponseDto createStarshipResponseDto(String id, String name, String crew, String length) {
        return StarshipResponseDto.builder()
                .id(id)
                .name(name)
                .crew(crew)
                .length(length)
                .model("YT-1300 light freighter")
                .manufacturer("Corellian Engineering Corporation")
                .costInCredits("100000")
                .passengers("6")
                .starshipClass("Light freighter")
                .build();
    }
}