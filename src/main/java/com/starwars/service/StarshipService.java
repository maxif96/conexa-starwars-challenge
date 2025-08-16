package com.starwars.service;

import com.starwars.dto.api.*;
import com.starwars.dto.response.PageResponseDto;
import com.starwars.dto.response.StarshipResponseDto;
import com.starwars.exception.ResourceNotFoundException;
import com.starwars.mapper.StarshipMapper;
import com.starwars.service.base.BaseStarWarsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StarshipService extends BaseStarWarsService {

    private final StarshipMapper starshipMapper;

    @Autowired
    public StarshipService(StarshipMapper starshipMapper) {
        this.starshipMapper = starshipMapper;
    }

    public PageResponseDto<StarshipResponseDto> listOrSearchStarships(String name, int page, int limit) {
        if (name != null && !name.trim().isEmpty()) {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/starships")
                    .queryParam("name", name.trim())
                    .build()
                    .toUriString();
            
            ParameterizedTypeReference<ApiEntityResponse<List<ApiDetailResult<StarshipApiDto>>>> typeRef =
                    new ParameterizedTypeReference<ApiEntityResponse<List<ApiDetailResult<StarshipApiDto>>>>() {};
            ApiEntityResponse<List<ApiDetailResult<StarshipApiDto>>> apiResponse = fetchApiData(url, typeRef);

            if (apiResponse != null && apiResponse.getResult() != null) {
                List<StarshipResponseDto> starships = mapDetailResultToResponse(apiResponse.getResult());
                return createManualPageResponse(starships, page, limit);
            }
        } else {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/starships")
                    .queryParam("page", page)
                    .queryParam("limit", limit)
                    .queryParam("expanded", "true")
                    .build()
                    .toUriString();

            ParameterizedTypeReference<ApiPageResponse<StarshipApiDto>> typeRef =
                    new ParameterizedTypeReference<ApiPageResponse<StarshipApiDto>>() {};
            ApiPageResponse<StarshipApiDto> apiResponse = fetchApiData(url, typeRef);

            if (apiResponse != null && apiResponse.getResults() != null) {
                List<StarshipResponseDto> starships = mapDetailResultToResponse(apiResponse.getResults());
                return createPageResponse(starships, apiResponse, page);
            }
        }
        return createEmptyPage();
    }

    public StarshipResponseDto getStarshipById(String id) {
        try {
            String url = baseUrl + "/starships/" + id;
            ParameterizedTypeReference<ApiEntityResponse<ApiDetailResult<StarshipApiDto>>> typeRef =
                    new ParameterizedTypeReference<ApiEntityResponse<ApiDetailResult<StarshipApiDto>>>() {};
            ApiEntityResponse<ApiDetailResult<StarshipApiDto>> apiResponse = fetchApiData(url, typeRef);

            if (apiResponse != null && apiResponse.getResult() != null && apiResponse.getResult().getProperties() != null) {
                ApiResult<StarshipApiDto> tempResult = new ApiResult<>();
                tempResult.setUid(apiResponse.getResult().getUid());
                tempResult.setProperties(apiResponse.getResult().getProperties());
                return starshipMapper.toResponseDtoFromDetail(tempResult);
            }
            
            throw new ResourceNotFoundException("Starship", "id", id);
        } catch (Exception e) {
            log.error("Error en getStarshipById para ID {}: ", id, e);
            throw new ResourceNotFoundException("Starship", "id", id);
        }
    }

    private List<StarshipResponseDto> mapDetailResultToResponse(List<ApiDetailResult<StarshipApiDto>> results) {
        return results.stream()
                .map(detailResult -> {
                    ApiResult<StarshipApiDto> tempResult = new ApiResult<>();
                    tempResult.setUid(detailResult.getUid());
                    tempResult.setProperties(detailResult.getProperties());
                    return starshipMapper.toResponseDtoFromDetail(tempResult);
                })
                .collect(Collectors.toList());
    }
}