package com.starwars.films.service;

import com.starwars.films.dto.FilmApiDto;
import com.starwars.shared.dto.api.*;
import com.starwars.films.dto.FilmResponseDto;
import com.starwars.shared.dto.PageResponseDto;
import com.starwars.shared.exception.ResourceNotFoundException;
import com.starwars.films.mapper.FilmMapper;
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
public class FilmService extends BaseStarWarsService {

    private final FilmMapper filmMapper;

    public PageResponseDto<FilmResponseDto> listOrSearchFilms(String title, int page, int limit) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/films")
                .queryParam("page", page)
                .queryParam("limit", limit)
                .queryParam("expanded", "true");

        if (title != null && !title.trim().isEmpty()) {
            builder.queryParam("title", title.trim());
        }
        String url = builder.build().toUriString();

        ParameterizedTypeReference<ApiEntityResponse<List<ApiDetailResult<FilmApiDto>>>> typeRef =
                new ParameterizedTypeReference<ApiEntityResponse<List<ApiDetailResult<FilmApiDto>>>>() {};

        ApiEntityResponse<List<ApiDetailResult<FilmApiDto>>> apiResponse = fetchApiData(url, typeRef);

        if (apiResponse != null && apiResponse.getResult() != null) {
            List<FilmResponseDto> films = apiResponse.getResult().stream()
                    .map(detailResult -> {
                        ApiResult<FilmApiDto> tempResult = new ApiResult<>();
                        tempResult.setUid(detailResult.getUid());
                        tempResult.setProperties(detailResult.getProperties());
                        return filmMapper.toResponseDtoFromDetail(tempResult);
                    })
                    .collect(Collectors.toList());

            return createManualPageResponse(films, page, limit);
        }
        return createEmptyPage();
    }

    public FilmResponseDto getFilmById(String id) {
        log.info("Buscando película por ID: {}", id);
        try {
            String url = baseUrl + "/films/" + id;
            ParameterizedTypeReference<ApiEntityResponse<ApiDetailResult<FilmApiDto>>> typeRef =
                    new ParameterizedTypeReference<ApiEntityResponse<ApiDetailResult<FilmApiDto>>>() {};

            ApiEntityResponse<ApiDetailResult<FilmApiDto>> apiResponse = fetchApiData(url, typeRef);

            if (apiResponse != null && apiResponse.getResult() != null && apiResponse.getResult().getProperties() != null) {
                ApiResult<FilmApiDto> tempResult = new ApiResult<>();
                tempResult.setUid(apiResponse.getResult().getUid());
                tempResult.setProperties(apiResponse.getResult().getProperties());
                return filmMapper.toResponseDtoFromDetail(tempResult);
            }
            
            throw new ResourceNotFoundException("Film", "id", id);
        } catch (Exception e) {
            log.error("Error en getFilmById para ID {}: ", id, e);
            throw new ResourceNotFoundException("Film", "id", id);
        }
    }
}
