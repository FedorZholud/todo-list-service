package com.github.fedorzholud.todolistservice

import com.github.fedorzholud.todolistservice.todo.application.port.`in`.CreateTodoCommand
import com.github.fedorzholud.todolistservice.todo.domain.Todo
import com.github.fedorzholud.todolistservice.todo.domain.TodoId
import com.github.fedorzholud.todolistservice.todo.domain.TodoStatus
import java.time.OffsetDateTime
import kotlin.random.Random

private val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun createRandomString(length: Int): String =
    (1..length)
        .map { Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")

fun createTestTodo(
    id: TodoId = TodoId(),
    description: String = createRandomString(Random.nextInt(10, 35)),
    status: TodoStatus = TodoStatus.NOT_DONE,
    dueDatetime: OffsetDateTime = OffsetDateTime.now().plusDays(3),
    doneDatetime: OffsetDateTime? = null,
    creationDatetime: OffsetDateTime = OffsetDateTime.now(),
) = Todo(
    id = id,
    description = description,
    status = status,
    dueDatetime = dueDatetime,
    doneDatetime = when {
        status == TodoStatus.DONE && doneDatetime == null -> OffsetDateTime.now()
        else -> doneDatetime
    },
    creationDatetime = creationDatetime,
)

fun createTestCreateTodoCommand(
    description: String = createRandomString(Random.nextInt(10, 35)),
    dueDatetime: OffsetDateTime = OffsetDateTime.now().plusDays(3)
) = CreateTodoCommand(
    description = description,
    dueDatetime = dueDatetime,
)