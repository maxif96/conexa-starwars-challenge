package com.starwars.starships.controller;

import com.starwars.shared.dto.PageResponseDto;
import com.starwars.starships.dto.StarshipResponseDto;
import com.starwars.starships.service.StarshipService;
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
@RequestMapping("/starships")
@Tag(name = "D. Starships")
@Validated
@RequiredArgsConstructor
public class StarshipsController {

    private final StarshipService starshipService;

    @GetMapping
    @Operation(
        summary = "Listar o buscar naves espaciales", 
        description = "Obtiene una lista paginada de naves espaciales de Star Wars, o filtra por nombre"
    )
    public ResponseEntity<PageResponseDto<StarshipResponseDto>> listOrSearch(
            @Parameter(description = "Filtro por nombre (no sensible a mayúsculas)")
            @RequestParam(required = false) String name,
            @Parameter(description = "Número de página (comienza en 1)")
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @Parameter(description = "Número de resultados por página")
            @RequestParam(defaultValue = "10") @Min(1) int limit) {

        log.info("Request received for starships. Name: [{}], Page: [{}], Limit: [{}]", name, page, limit);
        PageResponseDto<StarshipResponseDto> result = starshipService.listOrSearchStarships(name, page, limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener nave espacial por ID", 
        description = "Obtiene una nave espacial específica de Star Wars por su ID único"
    )
    public ResponseEntity<StarshipResponseDto> getById(
            @Parameter(description = "ID único de la nave espacial")
            @PathVariable String id) {

        log.info("Fetching starship by id: {}", id);
        StarshipResponseDto starship = starshipService.getStarshipById(id);
        return ResponseEntity.ok(starship);
    }
}

