package com.github.fedorzholud.todolistservice.todo.domain

import java.time.OffsetDateTime
import java.util.*

class DueDatetimeCouldNotBeInPastException(
    val dueDatetime: OffsetDateTime,
    message: String? = null
) : IllegalArgumentException(message)

class TodoNotFoundException(
    val todoId: TodoId,
    message: String? = null
) : NoSuchElementException(message)
