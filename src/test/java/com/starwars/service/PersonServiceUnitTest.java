package com.starwars.service;

import com.starwars.people.dto.PersonApiDto;
import com.starwars.shared.dto.api.*;
import com.starwars.shared.dto.PageResponseDto;
import com.starwars.people.dto.PersonResponseDto;
import com.starwars.shared.exception.ResourceNotFoundException;
import com.starwars.people.mapper.PersonMapper;
import com.starwars.people.service.PersonService;
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
public class PersonServiceUnitTest {

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonService personService;

    @Before
    public void setUp() {
        // Configuramos el baseUrl que se inyecta por @Value
        ReflectionTestUtils.setField(personService, "baseUrl", "https://swapi.tech/api");
    }

    @Test
    public void listOrSearchPeople_WithNameParameter_ShouldReturnSearchResults() {
        // Arrange
        String searchName = "Luke";
        int page = 1;
        int limit = 10;

        PersonApiDto personApiDto = createPersonApiDto("1", "Luke Skywalker");
        ApiDetailResult<PersonApiDto> detailResult = createApiDetailResult("1", personApiDto);
        List<ApiDetailResult<PersonApiDto>> resultList = Arrays.asList(detailResult);
        ApiEntityResponse<List<ApiDetailResult<PersonApiDto>>> apiResponse = createApiEntityResponse(resultList);

        PersonResponseDto responseDto = createPersonResponseDto("1", "Luke Skywalker");

        when(personMapper.toResponseDtoFromDetail(any(ApiResult.class))).thenReturn(responseDto);

        // Simulamos el método fetchApiData del padre
        PersonService spyService = spy(personService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<PersonResponseDto> result = spyService.listOrSearchPeople(searchName, page, limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Luke Skywalker", result.getContent().get(0).getName());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
        assertFalse(result.isHasNext());
        assertFalse(result.isHasPrevious());

        verify(personMapper).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test
    public void listOrSearchPeople_WithNameParameter_NoResults_ShouldReturnEmptyPage() {
        // Arrange
        String searchName = "NonExistent";
        int page = 1;
        int limit = 10;

        ApiEntityResponse<List<ApiDetailResult<PersonApiDto>>> apiResponse = createApiEntityResponse(Collections.emptyList());

        PersonService spyService = spy(personService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<PersonResponseDto> result = spyService.listOrSearchPeople(searchName, page, limit);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    public void listOrSearchPeople_WithNameParameter_NullApiResponse_ShouldReturnEmptyPage() {
        // Arrange
        String searchName = "Luke";
        int page = 1;
        int limit = 10;

        PersonService spyService = spy(personService);
        doReturn(null).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<PersonResponseDto> result = spyService.listOrSearchPeople(searchName, page, limit);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    public void listOrSearchPeople_WithoutNameParameter_ShouldReturnPaginatedResults() {
        // Arrange
        int page = 1;
        int limit = 10;

        PersonApiDto personApiDto1 = createPersonApiDto("1", "Luke Skywalker");
        PersonApiDto personApiDto2 = createPersonApiDto("2", "Leia Organa");

        List<ApiDetailResult<PersonApiDto>> resultList = Arrays.asList(
                createApiDetailResult("1", personApiDto1),
                createApiDetailResult("2", personApiDto2)
        );

        ApiPageResponse<PersonApiDto> apiPageResponse = createApiPageResponse(resultList, 5, 50, "next_url", null);

        PersonResponseDto responseDto1 = createPersonResponseDto("1", "Luke Skywalker");
        PersonResponseDto responseDto2 = createPersonResponseDto("2", "Leia Organa");

        when(personMapper.toResponseDtoFromDetail(any(ApiResult.class)))
                .thenReturn(responseDto1)
                .thenReturn(responseDto2);

        PersonService spyService = spy(personService);
        doReturn(apiPageResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<PersonResponseDto> result = spyService.listOrSearchPeople(null, page, limit);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Luke Skywalker", result.getContent().get(0).getName());
        assertEquals("Leia Organa", result.getContent().get(1).getName());
        assertEquals(50L, result.getTotalElements());
        assertEquals(5, result.getTotalPages());
        assertTrue(result.isFirst());
        assertFalse(result.isLast());
        assertTrue(result.isHasNext());
        assertFalse(result.isHasPrevious());

        verify(personMapper, times(2)).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test
    public void listOrSearchPeople_WithEmptyName_ShouldReturnPaginatedResults() {
        // Arrange
        String emptyName = "   "; // Solo espacios en blanco
        int page = 1;
        int limit = 10;

        PersonApiDto personApiDto = createPersonApiDto("1", "Luke Skywalker");
        List<ApiDetailResult<PersonApiDto>> resultList = Arrays.asList(createApiDetailResult("1", personApiDto));
        ApiPageResponse<PersonApiDto> apiPageResponse = createApiPageResponse(resultList, 1, 1, null, null);

        PersonResponseDto responseDto = createPersonResponseDto("1", "Luke Skywalker");
        when(personMapper.toResponseDtoFromDetail(any(ApiResult.class))).thenReturn(responseDto);

        PersonService spyService = spy(personService);
        doReturn(apiPageResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PageResponseDto<PersonResponseDto> result = spyService.listOrSearchPeople(emptyName, page, limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(personMapper).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test
    public void getPersonById_ValidId_ShouldReturnPerson() {
        // Arrange
        String personId = "1";
        PersonApiDto personApiDto = createPersonApiDto("1", "Luke Skywalker");
        ApiDetailResult<PersonApiDto> detailResult = createApiDetailResult("1", personApiDto);
        ApiEntityResponse<ApiDetailResult<PersonApiDto>> apiResponse = createApiEntityResponse(detailResult);
        PersonResponseDto expectedResponse = createPersonResponseDto("1", "Luke Skywalker");

        when(personMapper.toResponseDtoFromDetail(any(ApiResult.class))).thenReturn(expectedResponse);

        PersonService spyService = spy(personService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PersonResponseDto result = spyService.getPersonById(personId);

        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Luke Skywalker", result.getName());
        verify(personMapper).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getPersonById_NullApiResponse_ShouldThrowException() {
        // Arrange
        String personId = "999";

        PersonService spyService = spy(personService);
        doReturn(null).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        spyService.getPersonById(personId);

        // Assert - Se espera ResourceNotFoundException
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getPersonById_NullResult_ShouldThrowException() {
        // Arrange
        String personId = "999";
        ApiEntityResponse<ApiDetailResult<PersonApiDto>> apiResponse = createApiEntityResponse((ApiDetailResult<PersonApiDto>) null);

        PersonService spyService = spy(personService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        spyService.getPersonById(personId);

        // Assert - Se espera ResourceNotFoundException
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getPersonById_NullProperties_ShouldThrowException() {
        // Arrange
        String personId = "999";
        ApiDetailResult<PersonApiDto> detailResult = new ApiDetailResult<>();
        detailResult.setUid("999");
        detailResult.setProperties(null);

        ApiEntityResponse<ApiDetailResult<PersonApiDto>> apiResponse = createApiEntityResponse(detailResult);

        PersonService spyService = spy(personService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        spyService.getPersonById(personId);

        // Assert - Se espera ResourceNotFoundException
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getPersonById_ApiThrowsException_ShouldThrowResourceNotFoundException() {
        // Arrange
        String personId = "1";

        PersonService spyService = spy(personService);
        doThrow(new RuntimeException("API Error")).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        spyService.getPersonById(personId);

        // Assert - Se espera ResourceNotFoundException
    }

    // Métodos auxiliares para crear objetos de prueba
    private PersonApiDto createPersonApiDto(String uid, String name) {
        PersonApiDto dto = new PersonApiDto();
        ReflectionTestUtils.setField(dto, "name", name);
        ReflectionTestUtils.setField(dto, "height", "172");
        ReflectionTestUtils.setField(dto, "mass", "77");
        ReflectionTestUtils.setField(dto, "hairColor", "blond");
        ReflectionTestUtils.setField(dto, "skinColor", "fair");
        ReflectionTestUtils.setField(dto, "eyeColor", "blue");
        ReflectionTestUtils.setField(dto, "birthYear", "19BBY");
        ReflectionTestUtils.setField(dto, "gender", "male");
        ReflectionTestUtils.setField(dto, "homeworld", "Tatooine");
        return dto;
    }

    private ApiDetailResult<PersonApiDto> createApiDetailResult(String uid, PersonApiDto properties) {
        ApiDetailResult<PersonApiDto> result = new ApiDetailResult<>();
        result.setUid(uid);
        result.setProperties(properties);
        return result;
    }

    private ApiEntityResponse<List<ApiDetailResult<PersonApiDto>>> createApiEntityResponse(List<ApiDetailResult<PersonApiDto>> result) {
        ApiEntityResponse<List<ApiDetailResult<PersonApiDto>>> response = new ApiEntityResponse<>();
        response.setResult(result);
        return response;
    }

    private ApiEntityResponse<ApiDetailResult<PersonApiDto>> createApiEntityResponse(ApiDetailResult<PersonApiDto> result) {
        ApiEntityResponse<ApiDetailResult<PersonApiDto>> response = new ApiEntityResponse<>();
        response.setResult(result);
        return response;
    }

    private ApiPageResponse<PersonApiDto> createApiPageResponse(List<ApiDetailResult<PersonApiDto>> results,
                                                                Integer totalPages, Integer totalRecords,
                                                                String next, String previous) {
        ApiPageResponse<PersonApiDto> response = new ApiPageResponse<>();
        response.setResults(results);
        response.setTotalPages(totalPages);
        response.setTotalRecords(totalRecords);
        response.setNext(next);
        response.setPrevious(previous);
        return response;
    }

    private PersonResponseDto createPersonResponseDto(String id, String name) {
        return PersonResponseDto.builder()
                .id(id)
                .name(name)
                .height("172")
                .mass("77")
                .hairColor("blond")
                .skinColor("fair")
                .eyeColor("blue")
                .birthYear("19BBY")
                .gender("male")
                .homeworld("Tatooine")
                .build();
    }
}