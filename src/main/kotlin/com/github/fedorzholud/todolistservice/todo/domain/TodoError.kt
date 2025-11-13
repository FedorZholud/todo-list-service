package com.github.fedorzholud.todolistservice.todo.domain

import java.time.OffsetDateTime

class DueDatetimeCouldNotBeInPastException(
    val dueDatetime: OffsetDateTime,
    message: String? = null
) : IllegalArgumentException(message)
