package com.starwars.people.service;

import com.starwars.people.dto.PersonApiDto;
import com.starwars.shared.dto.api.*;
import com.starwars.shared.dto.PageResponseDto;
import com.starwars.people.dto.PersonResponseDto;
import com.starwars.shared.exception.ResourceNotFoundException;
import com.starwars.people.mapper.PersonMapper;
import com.starwars.shared.service.BaseStarWarsService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService extends BaseStarWarsService {

    private final PersonMapper personMapper;

    public PageResponseDto<PersonResponseDto> listOrSearchPeople(String name, int page, int limit) {
        if (name != null && !name.trim().isEmpty()) {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/people")
                    .queryParam("name", name.trim())
                    .build()
                    .toUriString();

            ParameterizedTypeReference<ApiEntityResponse<List<ApiDetailResult<PersonApiDto>>>> typeRef =
                    new ParameterizedTypeReference<ApiEntityResponse<List<ApiDetailResult<PersonApiDto>>>>() {};
            ApiEntityResponse<List<ApiDetailResult<PersonApiDto>>> apiResponse = fetchApiData(url, typeRef);

            if (apiResponse != null && apiResponse.getResult() != null) {
                List<PersonResponseDto> people = mapDetailResultToResponse(apiResponse.getResult());
                return createManualPageResponse(people, page, limit);
            }
        } else {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/people")
                    .queryParam("page", page)
                    .queryParam("limit", limit)
                    .queryParam("expanded", "true")
                    .build()
                    .toUriString();

            ParameterizedTypeReference<ApiPageResponse<PersonApiDto>> typeRef =
                    new ParameterizedTypeReference<ApiPageResponse<PersonApiDto>>() {};
            ApiPageResponse<PersonApiDto> apiResponse = fetchApiData(url, typeRef);

            if (apiResponse != null && apiResponse.getResults() != null) {
                List<PersonResponseDto> people = mapDetailResultToResponse(apiResponse.getResults());
                return createPageResponse(people, apiResponse, page);
            }
        }
        return createEmptyPage();
    }

    public PersonResponseDto getPersonById(String id) {
        try {
            String url = baseUrl + "/people/" + id;
            ParameterizedTypeReference<ApiEntityResponse<ApiDetailResult<PersonApiDto>>> typeRef =
                    new ParameterizedTypeReference<ApiEntityResponse<ApiDetailResult<PersonApiDto>>>() {};
            ApiEntityResponse<ApiDetailResult<PersonApiDto>> apiResponse = fetchApiData(url, typeRef);

            if (apiResponse != null && apiResponse.getResult() != null && apiResponse.getResult().getProperties() != null) {
                ApiResult<PersonApiDto> tempResult = new ApiResult<>();
                tempResult.setUid(apiResponse.getResult().getUid());
                tempResult.setProperties(apiResponse.getResult().getProperties());
                return personMapper.toResponseDtoFromDetail(tempResult);
            }

            throw new ResourceNotFoundException("Person", "id", id);
        } catch (Exception e) {
            log.error("Error en getPersonById para ID {}: ", id, e);
            throw new ResourceNotFoundException("Person", "id", id);
        }
    }

    private List<PersonResponseDto> mapDetailResultToResponse(List<ApiDetailResult<PersonApiDto>> results) {
        return results.stream()
                .map(detailResult -> {
                    ApiResult<PersonApiDto> tempResult = new ApiResult<>();
                    tempResult.setUid(detailResult.getUid());
                    tempResult.setProperties(detailResult.getProperties());
                    return personMapper.toResponseDtoFromDetail(tempResult);
                })
                .collect(Collectors.toList());
    }
}
