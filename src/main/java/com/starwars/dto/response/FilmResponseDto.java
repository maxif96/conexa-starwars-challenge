package com.starwars.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilmResponseDto {
    private String id;
    private String title;
    private int episodeId;
    private String openingCrawl;
    private String director;
    private String producer;
    private String releaseDate;
}