package com.github.fedorzholud.todolistservice.todo.domain

import java.time.OffsetDateTime
import java.util.UUID

data class Todo(
    val id: TodoId = TodoId(),
    val description: String?,
    val status: TodoStatus,
    val dueDatetime: OffsetDateTime,
    val doneDatetime: OffsetDateTime? = null,
    val creationDatetime: OffsetDateTime = OffsetDateTime.now(),
)

data class TodoId(
    val value: UUID = UUID.randomUUID()
)

enum class TodoStatus(val value: String) {
    DONE("done"),
    NOT_DONE("not done"),
    PAST_DUE("past due")
}