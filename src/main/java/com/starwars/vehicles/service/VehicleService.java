package com.starwars.vehicles.service;

import com.starwars.shared.dto.api.*;
import com.starwars.shared.dto.PageResponseDto;
import com.starwars.vehicles.dto.VehicleApiDto;
import com.starwars.vehicles.dto.VehicleResponseDto;
import com.starwars.shared.exception.ResourceNotFoundException;
import com.starwars.vehicles.mapper.VehicleMapper;
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
public class VehicleService extends BaseStarWarsService {

    private final VehicleMapper vehicleMapper;

    public PageResponseDto<VehicleResponseDto> listOrSearchVehicles(String name, int page, int limit) {
        if (name != null && !name.trim().isEmpty()) {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/vehicles")
                    .queryParam("name", name.trim())
                    .build()
                    .toUriString();
            
            ParameterizedTypeReference<ApiEntityResponse<List<ApiDetailResult<VehicleApiDto>>>> typeRef =
                    new ParameterizedTypeReference<ApiEntityResponse<List<ApiDetailResult<VehicleApiDto>>>>() {};
            ApiEntityResponse<List<ApiDetailResult<VehicleApiDto>>> apiResponse = fetchApiData(url, typeRef);

            if (apiResponse != null && apiResponse.getResult() != null) {
                List<VehicleResponseDto> vehicles = mapDetailResultToResponse(apiResponse.getResult());
                return createManualPageResponse(vehicles, page, limit);
            }
        } else {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/vehicles")
                    .queryParam("page", page)
                    .queryParam("limit", limit)
                    .queryParam("expanded", "true")
                    .build()
                    .toUriString();

            ParameterizedTypeReference<ApiPageResponse<VehicleApiDto>> typeRef =
                    new ParameterizedTypeReference<ApiPageResponse<VehicleApiDto>>() {};
            ApiPageResponse<VehicleApiDto> apiResponse = fetchApiData(url, typeRef);

            if (apiResponse != null && apiResponse.getResults() != null) {
                List<VehicleResponseDto> vehicles = mapDetailResultToResponse(apiResponse.getResults());
                return createPageResponse(vehicles, apiResponse, page);
            }
        }
        return createEmptyPage();
    }

    public VehicleResponseDto getVehicleById(String id) {
        try {
            String url = baseUrl + "/vehicles/" + id;
            ParameterizedTypeReference<ApiEntityResponse<ApiDetailResult<VehicleApiDto>>> typeRef =
                    new ParameterizedTypeReference<ApiEntityResponse<ApiDetailResult<VehicleApiDto>>>() {};
            ApiEntityResponse<ApiDetailResult<VehicleApiDto>> apiResponse = fetchApiData(url, typeRef);
            
            if (apiResponse != null && apiResponse.getResult() != null && apiResponse.getResult().getProperties() != null) {
                ApiResult<VehicleApiDto> tempResult = new ApiResult<>();
                tempResult.setUid(apiResponse.getResult().getUid());
                tempResult.setProperties(apiResponse.getResult().getProperties());
                return vehicleMapper.toResponseDtoFromDetail(tempResult);
            }
            
            throw new ResourceNotFoundException("Vehicle", "id", id);
        } catch (Exception e) {
            log.error("Error en getVehicleById para ID {}: ", id, e);
            throw new ResourceNotFoundException("Vehicle", "id", id);
        }
    }

    private List<VehicleResponseDto> mapDetailResultToResponse(List<ApiDetailResult<VehicleApiDto>> results) {
        return results.stream()
                .map(detailResult -> {
                    ApiResult<VehicleApiDto> tempResult = new ApiResult<>();
                    tempResult.setUid(detailResult.getUid());
                    tempResult.setProperties(detailResult.getProperties());
                    return vehicleMapper.toResponseDtoFromDetail(tempResult);
                })
                .collect(Collectors.toList());
    }
}

