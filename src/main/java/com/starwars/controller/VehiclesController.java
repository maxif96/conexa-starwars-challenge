package com.starwars.controller;

import com.starwars.dto.response.PageResponseDto;
import com.starwars.dto.response.VehicleResponseDto;
import com.starwars.service.VehicleService;
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
@RequestMapping("/vehicles")
@Tag(name = "E. Vehicles")
@Validated
@RequiredArgsConstructor
public class VehiclesController {

    private final VehicleService vehicleService;

    @GetMapping
    @Operation(
        summary = "Listar o buscar vehículos", 
        description = "Obtiene una lista paginada de vehículos de Star Wars, o filtra por nombre"
    )
    public ResponseEntity<PageResponseDto<VehicleResponseDto>> listOrSearch(
            @Parameter(description = "Filtro por nombre (no sensible a mayúsculas)")
            @RequestParam(required = false) String name,
            @Parameter(description = "Número de página (comienza en 1)")
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @Parameter(description = "Número de resultados por página")
            @RequestParam(defaultValue = "10") @Min(1) int limit) {

        log.info("Request received for vehicles. Name: [{}], Page: [{}], Limit: [{}]", name, page, limit);
        PageResponseDto<VehicleResponseDto> result = vehicleService.listOrSearchVehicles(name, page, limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener vehículo por ID", 
        description = "Obtiene un vehículo específico de Star Wars por su ID único"
    )
    public ResponseEntity<VehicleResponseDto> getById(
            @Parameter(description = "ID único del vehículo")
            @PathVariable String id) {

        log.info("Fetching vehicle by id: {}", id);
        VehicleResponseDto vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(vehicle);
    }
}