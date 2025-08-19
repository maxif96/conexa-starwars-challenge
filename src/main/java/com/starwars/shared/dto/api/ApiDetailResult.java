package com.starwars.shared.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class ApiDetailResult<T> {
    @JsonProperty("properties")
    private T properties;

    @JsonProperty("description")
    private String description;

    @JsonProperty("uid")
    private String uid;
}
