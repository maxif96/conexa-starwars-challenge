package com.starwars.films.mapper;

import com.starwars.shared.dto.api.ApiResult;
import com.starwars.films.dto.FilmApiDto;
import com.starwars.films.dto.FilmResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FilmMapper {
    @Mapping(source = "uid", target = "id")
    @Mapping(source = "properties.title", target = "title")
    @Mapping(source = "properties.episodeId", target = "episodeId")
    @Mapping(source = "properties.openingCrawl", target = "openingCrawl")
    @Mapping(source = "properties.director", target = "director")
    @Mapping(source = "properties.producer", target = "producer")
    @Mapping(source = "properties.releaseDate", target = "releaseDate")
    FilmResponseDto toResponseDtoFromDetail(ApiResult<FilmApiDto> apiResult);
}
