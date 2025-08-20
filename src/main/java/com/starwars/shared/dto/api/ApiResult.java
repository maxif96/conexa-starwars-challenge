package com.starwars.shared.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiResult<T> {
    @JsonProperty("uid")
    private String uid;

    @JsonProperty("name")
    private String name;

    @JsonProperty("url")
    private String url;

    @JsonProperty("properties")
    private T properties;
}

