package com.github.fedorzholud.todolistservice.todo.adapter.`in`

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class CreateTodoDto(
    @JsonProperty("description", required = true) val description: String,
    @JsonProperty("dueDatetime", required = true) val dueDatetime: OffsetDateTime,
)