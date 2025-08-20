package com.starwars.shared.service;

import com.starwars.shared.dto.api.ApiPageResponse;
import com.starwars.shared.dto.PageResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class BaseStarWarsService {

    @Autowired
    protected RestTemplate restTemplate;

    @Value("${swapi.api.base-url}")
    protected String baseUrl;

    /**
     * Método único y genérico para todas las llamadas a la API.
     * Ejecuta una petición a la URL dada y la deserializa usando el TypeReference proporcionado.
     */
    public <T> T fetchApiData(String url, ParameterizedTypeReference<T> typeRef) {
        try {
            log.debug("Fetching API data from: {}", url);
            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    typeRef
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error al obtener datos de la URL {}: ", url, e);
            return null;
        }
    }

    /**
     * Crea una respuesta de página manualmente cuando la API externa no provee metadatos de paginación.
     */
    public <T> PageResponseDto<T> createManualPageResponse(List<T> fullList, int page, int limit) {
        if (fullList == null) {
            fullList = new ArrayList<>();
        }
        int totalElements = fullList.size();
        if (totalElements == 0) {
            return createEmptyPage();
        }

        int totalPages = (int) Math.ceil((double) totalElements / limit);
        int start = (page - 1) * limit;

        if (start >= totalElements) {
            return createEmptyPage();
        }

        int end = Math.min(start + limit, totalElements);
        List<T> paginatedList = fullList.subList(start, end);

        PageResponseDto<T> pageResponse = new PageResponseDto<>();
        pageResponse.setContent(paginatedList);
        pageResponse.setPage(page);
        pageResponse.setSize(paginatedList.size());
        pageResponse.setTotalElements((long) totalElements);
        pageResponse.setTotalPages(totalPages);
        pageResponse.setFirst(page == 1);
        pageResponse.setLast(page >= totalPages);
        pageResponse.setHasNext(page < totalPages);
        pageResponse.setHasPrevious(page > 1);
        return pageResponse;
    }

    /**
     * Crea una respuesta de página vacía estandarizada.
     */
    protected <T> PageResponseDto<T> createEmptyPage() {
        PageResponseDto<T> pageResponse = new PageResponseDto<>();
        pageResponse.setContent(new ArrayList<>());
        pageResponse.setTotalPages(0);
        pageResponse.setTotalElements(0L);
        pageResponse.setPage(1);
        pageResponse.setSize(0);
        pageResponse.setFirst(true);
        pageResponse.setLast(true);
        pageResponse.setHasNext(false);
        pageResponse.setHasPrevious(false);
        return pageResponse;
    }

    protected <T> PageResponseDto<T> createPageResponse(List<T> content, ApiPageResponse<?> apiResponse, int page) {
        PageResponseDto<T> pageResponse = new PageResponseDto<>();
        pageResponse.setContent(content);
        // Usamos los totales que nos da la API
        pageResponse.setTotalPages(apiResponse.getTotalPages() != null ? apiResponse.getTotalPages() : 1);
        pageResponse.setTotalElements(apiResponse.getTotalRecords() != null ? apiResponse.getTotalRecords().longValue() : content.size());
        pageResponse.setPage(page);
        pageResponse.setSize(content.size());
        pageResponse.setFirst(page <= 1);
        // Usamos los campos 'next' y 'previous' de la API para determinar el resto
        pageResponse.setLast(apiResponse.getNext() == null);
        pageResponse.setHasNext(apiResponse.getNext() != null);
        pageResponse.setHasPrevious(apiResponse.getPrevious() != null);

        return pageResponse;
    }
}

