package com.starwars.starships.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StarshipApiDto {
    private String name;
    private String model;
    private String manufacturer;
    @JsonProperty("cost_in_credits")
    private String costInCredits;
    private String length;
    private String crew;
    private String passengers;
    @JsonProperty("starship_class")
    private String starshipClass;
    private String url;
}

