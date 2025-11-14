package com.github.fedorzholud.todolistservice.todo.adapter.`in`

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import java.util.UUID

data class TodoDto(
    @JsonProperty("id", required = true) val id: UUID,
    @JsonProperty("description", required = true) val description: String,
    @JsonProperty("status", required = true) val status: String,
    @JsonProperty("dueDatetime", required = true) val dueDatetime: OffsetDateTime,
    @JsonProperty("doneDatetime") val doneDatetime: OffsetDateTime?,
    @JsonProperty("creationDatetime", required = true) val creationDatetime: OffsetDateTime,
)
