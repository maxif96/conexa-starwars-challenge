package com.starwars.people.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonResponseDto {
    private String id;
    private String name;
    private String height;
    private String mass;
    private String hairColor;
    private String skinColor;
    private String eyeColor;
    private String birthYear;
    private String gender;
    private String homeworld;
}

