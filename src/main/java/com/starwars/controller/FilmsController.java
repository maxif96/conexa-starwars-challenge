package com.starwars.controller;

import com.starwars.dto.response.FilmResponseDto;
import com.starwars.dto.response.PageResponseDto;
import com.starwars.service.FilmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequestMapping("/films")
@Tag(name = "B. Films")
@Validated
@RequiredArgsConstructor
public class FilmsController {

    private final FilmService filmService;

    @GetMapping
    @Operation(
        summary = "Listar o buscar películas", 
        description = "Obtiene una lista paginada de películas de Star Wars, o filtra por título"
    )
    public ResponseEntity<PageResponseDto<FilmResponseDto>> listOrSearch(
            @Parameter(description = "Filtro por título (no sensible a mayúsculas)")
            @RequestParam(required = false) String title,
            @Parameter(description = "Número de página (comienza en 1)")
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @Parameter(description = "Número de resultados por página")
            @RequestParam(defaultValue = "10") @Min(1) int limit) {

        log.info("Request received for films. Title: [{}], Page: [{}], Limit: [{}]", title, page, limit);
        PageResponseDto<FilmResponseDto> result = filmService.listOrSearchFilms(title, page, limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener película por ID", 
        description = "Obtiene una película específica de Star Wars por su ID único"
    )
    public ResponseEntity<FilmResponseDto> getById(
            @Parameter(description = "ID único de la película")
            @PathVariable String id) {

        log.info("Fetching film by id: {}", id);
        FilmResponseDto film = filmService.getFilmById(id);
        return ResponseEntity.ok(film);
    }
}