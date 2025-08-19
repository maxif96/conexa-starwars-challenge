package com.starwars.service;

import com.starwars.dto.api.*;
import com.starwars.dto.response.PageResponseDto;
import com.starwars.dto.response.VehicleResponseDto;
import com.starwars.exception.ResourceNotFoundException;
import com.starwars.mapper.VehicleMapper;
import com.starwars.service.VehicleService;
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
public class VehicleServiceUnitTest {

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleService vehicleService;

    @Before
    public void setUp() {
        // Configuramos el baseUrl que se inyecta por @Value
        ReflectionTestUtils.setField(vehicleService, "baseUrl", "https://swapi.tech/api");
    }

    @Test
    public void listOrSearchVehicles_WithNameParameter_ShouldReturnSearchResults() {
        // Arrange
        String searchName = "Sand";
        int page = 1;
        int limit = 10;

        VehicleApiDto vehicleApiDto = createVehicleApiDto("4", "Sand Crawler", "30", "36.8");
        ApiDetailResult<VehicleApiDto> detailResult = createApiDetailResult("4", vehicleApiDto);
        List<ApiDetailResult<VehicleApiDto>> resultList = Arrays.asList(detailResult);
        ApiEntityResponse<List<ApiDetailResult<VehicleApiDto>>> apiResponse = createApiEntityResponse(resultList);

        VehicleResponseDto responseDto = createVehicleResponseDto("4", "Sand Crawler", "30", "36.8");

        when(vehicleMapper.toResponseDtoFromDetail(any(ApiResult.class))).thenReturn(responseDto);

        // Simulamos el método fetchApiData del padre
        VehicleService spyService = spy(vehicleService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<VehicleResponseDto> result = spyService.listOrSearchVehicles(searchName, page, limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Sand Crawler", result.getContent().get(0).getName());
        assertEquals("30", result.getContent().get(0).getCrew());
        assertEquals("36.8", result.getContent().get(0).getLength());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
        assertFalse(result.isHasNext());
        assertFalse(result.isHasPrevious());

        verify(vehicleMapper).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test
    public void listOrSearchVehicles_WithNameParameter_NoResults_ShouldReturnEmptyPage() {
        // Arrange
        String searchName = "NonExistent";
        int page = 1;
        int limit = 10;

        ApiEntityResponse<List<ApiDetailResult<VehicleApiDto>>> apiResponse = createApiEntityResponse(Collections.emptyList());

        VehicleService spyService = spy(vehicleService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<VehicleResponseDto> result = spyService.listOrSearchVehicles(searchName, page, limit);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    public void listOrSearchVehicles_WithNameParameter_NullApiResponse_ShouldReturnEmptyPage() {
        // Arrange
        String searchName = "Sand";
        int page = 1;
        int limit = 10;

        VehicleService spyService = spy(vehicleService);
        doReturn(null).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<VehicleResponseDto> result = spyService.listOrSearchVehicles(searchName, page, limit);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    public void listOrSearchVehicles_WithoutNameParameter_ShouldReturnPaginatedResults() {
        // Arrange
        int page = 1;
        int limit = 10;

        VehicleApiDto vehicleApiDto1 = createVehicleApiDto("4", "Sand Crawler", "30", "36.8");
        VehicleApiDto vehicleApiDto2 = createVehicleApiDto("6", "T-16 skyhopper", "1", "10.4");

        List<ApiDetailResult<VehicleApiDto>> resultList = Arrays.asList(
                createApiDetailResult("4", vehicleApiDto1),
                createApiDetailResult("6", vehicleApiDto2)
        );

        ApiPageResponse<VehicleApiDto> apiPageResponse = createApiPageResponse(resultList, 4, 39, "next_url", null);

        VehicleResponseDto responseDto1 = createVehicleResponseDto("4", "Sand Crawler", "30", "36.8");
        VehicleResponseDto responseDto2 = createVehicleResponseDto("6", "T-16 skyhopper", "1", "10.4");

        when(vehicleMapper.toResponseDtoFromDetail(any(ApiResult.class)))
                .thenReturn(responseDto1)
                .thenReturn(responseDto2);

        VehicleService spyService = spy(vehicleService);
        doReturn(apiPageResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<VehicleResponseDto> result = spyService.listOrSearchVehicles(null, page, limit);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Sand Crawler", result.getContent().get(0).getName());
        assertEquals("T-16 skyhopper", result.getContent().get(1).getName());
        assertEquals(39L, result.getTotalElements());
        assertEquals(4, result.getTotalPages());
        assertTrue(result.isFirst());
        assertFalse(result.isLast());
        assertTrue(result.isHasNext());
        assertFalse(result.isHasPrevious());

        verify(vehicleMapper, times(2)).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test
    public void listOrSearchVehicles_WithEmptyName_ShouldReturnPaginatedResults() {
        // Arrange
        String emptyName = "   "; // Solo espacios en blanco
        int page = 1;
        int limit = 10;

        VehicleApiDto vehicleApiDto = createVehicleApiDto("4", "Sand Crawler", "30", "36.8");
        List<ApiDetailResult<VehicleApiDto>> resultList = Arrays.asList(createApiDetailResult("4", vehicleApiDto));

        ApiPageResponse<VehicleApiDto> apiPageResponse = createApiPageResponse(resultList, 1, 1, null, null);

        VehicleResponseDto responseDto = createVehicleResponseDto("4", "Sand Crawler", "30", "36.8");
        when(vehicleMapper.toResponseDtoFromDetail(any(ApiResult.class))).thenReturn(responseDto);

        VehicleService spyService = spy(vehicleService);
        doReturn(apiPageResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<VehicleResponseDto> result = spyService.listOrSearchVehicles(emptyName, page, limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Sand Crawler", result.getContent().get(0).getName());
        verify(vehicleMapper).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test
    public void getVehicleById_ValidId_ShouldReturnVehicle() {
        // Arrange
        String vehicleId = "4";
        VehicleApiDto vehicleApiDto = createVehicleApiDto("4", "Sand Crawler", "30", "36.8");
        ApiDetailResult<VehicleApiDto> detailResult = createApiDetailResult("4", vehicleApiDto);
        ApiEntityResponse<ApiDetailResult<VehicleApiDto>> apiResponse = createApiEntityResponse(detailResult);

        VehicleResponseDto responseDto = createVehicleResponseDto("4", "Sand Crawler", "30", "36.8");

        when(vehicleMapper.toResponseDtoFromDetail(any(ApiResult.class))).thenReturn(responseDto);

        VehicleService spyService = spy(vehicleService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        VehicleResponseDto result = spyService.getVehicleById(vehicleId);

        // Assert
        assertNotNull(result);
        assertEquals("4", result.getId());
        assertEquals("Sand Crawler", result.getName());
        assertEquals("30", result.getCrew());
        assertEquals("36.8", result.getLength());

        verify(vehicleMapper).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getVehicleById_NullApiResponse_ShouldThrowException() {
        // Arrange
        String vehicleId = "999";

        VehicleService spyService = spy(vehicleService);
        doReturn(null).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        spyService.getVehicleById(vehicleId);

        // Assert - Se espera ResourceNotFoundException
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getVehicleById_NullResult_ShouldThrowException() {
        // Arrange
        String vehicleId = "999";
        ApiEntityResponse<ApiDetailResult<VehicleApiDto>> apiResponse = createApiEntityResponse((ApiDetailResult<VehicleApiDto>) null);

        VehicleService spyService = spy(vehicleService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        spyService.getVehicleById(vehicleId);

        // Assert - Se espera ResourceNotFoundException
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getVehicleById_NullProperties_ShouldThrowException() {
        // Arrange
        String vehicleId = "999";
        ApiDetailResult<VehicleApiDto> detailResult = new ApiDetailResult<>();
        detailResult.setUid("999");
        detailResult.setProperties(null);

        ApiEntityResponse<ApiDetailResult<VehicleApiDto>> apiResponse = createApiEntityResponse(detailResult);

        VehicleService spyService = spy(vehicleService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        spyService.getVehicleById(vehicleId);

        // Assert - Se espera ResourceNotFoundException
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getVehicleById_ApiThrowsException_ShouldThrowResourceNotFoundException() {
        // Arrange
        String vehicleId = "1";

        VehicleService spyService = spy(vehicleService);
        doThrow(new RuntimeException("API Error")).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        spyService.getVehicleById(vehicleId);

        // Assert - Se espera ResourceNotFoundException
    }

    // Métodos auxiliares para crear objetos de prueba
    private VehicleApiDto createVehicleApiDto(String uid, String name, String crew, String length) {
        VehicleApiDto dto = new VehicleApiDto();
        ReflectionTestUtils.setField(dto, "name", name);
        ReflectionTestUtils.setField(dto, "crew", crew);
        ReflectionTestUtils.setField(dto, "length", length);
        ReflectionTestUtils.setField(dto, "model", "Digger Crawler");
        ReflectionTestUtils.setField(dto, "manufacturer", "Corellia Mining Corporation");
        ReflectionTestUtils.setField(dto, "costInCredits", "150000");
        ReflectionTestUtils.setField(dto, "passengers", "30");
        ReflectionTestUtils.setField(dto, "vehicleClass", "wheeled");
        return dto;
    }

    private ApiDetailResult<VehicleApiDto> createApiDetailResult(String uid, VehicleApiDto properties) {
        ApiDetailResult<VehicleApiDto> result = new ApiDetailResult<>();
        result.setUid(uid);
        result.setProperties(properties);
        return result;
    }

    private ApiEntityResponse<List<ApiDetailResult<VehicleApiDto>>> createApiEntityResponse(List<ApiDetailResult<VehicleApiDto>> result) {
        ApiEntityResponse<List<ApiDetailResult<VehicleApiDto>>> response = new ApiEntityResponse<>();
        response.setResult(result);
        return response;
    }

    private ApiEntityResponse<ApiDetailResult<VehicleApiDto>> createApiEntityResponse(ApiDetailResult<VehicleApiDto> result) {
        ApiEntityResponse<ApiDetailResult<VehicleApiDto>> response = new ApiEntityResponse<>();
        response.setResult(result);
        return response;
    }

    private ApiPageResponse<VehicleApiDto> createApiPageResponse(List<ApiDetailResult<VehicleApiDto>> results,
                                                                 Integer totalPages, Integer totalRecords,
                                                                 String next, String previous) {
        ApiPageResponse<VehicleApiDto> response = new ApiPageResponse<>();
        response.setResults(results);
        response.setTotalPages(totalPages);
        response.setTotalRecords(totalRecords);
        response.setNext(next);
        response.setPrevious(previous);
        return response;
    }

    private VehicleResponseDto createVehicleResponseDto(String id, String name, String crew, String length) {
        return VehicleResponseDto.builder()
                .id(id)
                .name(name)
                .crew(crew)
                .length(length)
                .model("Digger Crawler")
                .manufacturer("Corellia Mining Corporation")
                .costInCredits("150000")
                .passengers("30")
                .vehicleClass("wheeled")
                .build();
    }
}