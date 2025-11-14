package com.github.fedorzholud.todolistservice.todo.adapter.`in`

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.fedorzholud.todolistservice.todo.domain.TodoStatus

data class UpdateTodoDto(
    @JsonProperty("description") val description: String? = null,
    @JsonProperty("status") val status: TodoStatus? = null,
)