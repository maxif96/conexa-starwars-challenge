package com.starwars.starships.mapper;

import com.starwars.shared.dto.api.ApiResult;
import com.starwars.starships.dto.StarshipApiDto;
import com.starwars.starships.dto.StarshipResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StarshipMapper {
    @Mapping(source = "uid", target = "id")
    @Mapping(source = "properties.name", target = "name")
    @Mapping(source = "properties.model", target = "model")
    @Mapping(source = "properties.manufacturer", target = "manufacturer")
    @Mapping(source = "properties.costInCredits", target = "costInCredits")
    @Mapping(source = "properties.length", target = "length")
    @Mapping(source = "properties.crew", target = "crew")
    @Mapping(source = "properties.passengers", target = "passengers")
    @Mapping(source = "properties.starshipClass", target = "starshipClass")
    StarshipResponseDto toResponseDtoFromDetail(ApiResult<StarshipApiDto> apiResult);
}
