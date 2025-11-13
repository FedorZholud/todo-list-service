package com.github.fedorzholud.todolistservice.rest

import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorDto(
    @JsonProperty(value = "message", required = true) val message: String,
)
