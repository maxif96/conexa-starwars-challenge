package com.starwars.controller;

import com.starwars.dto.response.PageResponseDto;
import com.starwars.dto.response.PersonResponseDto;
import com.starwars.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequestMapping("/people")
@Tag(name = "C. People")
@Validated
public class PeopleController {

    @Autowired
    private PersonService personService;

    @GetMapping
    @Operation(
        summary = "Listar o buscar personajes", 
        description = "Obtiene una lista paginada de personajes de Star Wars, o filtra por nombre"
    )
    public ResponseEntity<PageResponseDto<PersonResponseDto>> listOrSearch(
            @Parameter(description = "Filtro por nombre (no sensible a mayúsculas)")
            @RequestParam(required = false) String name,
            @Parameter(description = "Número de página (comienza en 1)")
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @Parameter(description = "Número de resultados por página")
            @RequestParam(defaultValue = "10") @Min(1) int limit) {

        log.info("Request received for people. Name: [{}], Page: [{}], Limit: [{}]", name, page, limit);
        PageResponseDto<PersonResponseDto> result = personService.listOrSearchPeople(name, page, limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener personaje por ID", 
        description = "Obtiene un personaje específico de Star Wars por su ID único"
    )
    public ResponseEntity<PersonResponseDto> getById(
            @Parameter(description = "ID único del personaje")
            @PathVariable String id) {

        log.info("Fetching person by id: {}", id);
        PersonResponseDto person = personService.getPersonById(id);
        return ResponseEntity.ok(person);
    }
}