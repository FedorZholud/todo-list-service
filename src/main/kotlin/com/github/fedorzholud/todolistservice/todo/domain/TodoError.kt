package com.github.fedorzholud.todolistservice.todo.domain

import java.time.OffsetDateTime

class DueDatetimeCouldNotBeInPastException(
    val dueDatetime: OffsetDateTime,
    message: String? = null
) : IllegalArgumentException(message)

class TodoNotFoundException(
    val todoId: TodoId,
    message: String? = null
) : NoSuchElementException(message)

class PastDueTodoModificationForbiddenException(
    val todoId: TodoId,
    message: String? = null
) : IllegalStateException(message)

class UpdateTodoStatusToPastDueForbiddenException(
    val todoId: TodoId,
    message: String? = null
) : IllegalArgumentException(message)
