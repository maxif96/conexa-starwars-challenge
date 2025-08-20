package com.starwars.people.mapper;

import com.starwars.shared.dto.api.ApiResult;
import com.starwars.people.dto.PersonApiDto;
import com.starwars.people.dto.PersonResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    @Mapping(source = "uid", target = "id")
    @Mapping(source = "properties.name", target = "name")
    @Mapping(source = "properties.height", target = "height")
    @Mapping(source = "properties.mass", target = "mass")
    @Mapping(source = "properties.hairColor", target = "hairColor")
    @Mapping(source = "properties.skinColor", target = "skinColor")
    @Mapping(source = "properties.eyeColor", target = "eyeColor")
    @Mapping(source = "properties.birthYear", target = "birthYear")
    @Mapping(source = "properties.gender", target = "gender")
    @Mapping(source = "properties.homeworld", target = "homeworld")
    PersonResponseDto toResponseDtoFromDetail(ApiResult<PersonApiDto> apiResult);
}

