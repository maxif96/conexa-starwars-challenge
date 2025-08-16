package com.starwars.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiEntityResponse<T> {
    @JsonProperty("message")
    private String message;

    @JsonProperty("result")
    private T result;
}