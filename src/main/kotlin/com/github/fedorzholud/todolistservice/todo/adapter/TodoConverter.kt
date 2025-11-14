package com.github.fedorzholud.todolistservice.todo.adapter

import com.github.fedorzholud.todolistservice.todo.adapter.`in`.CreateTodoDto
import com.github.fedorzholud.todolistservice.todo.adapter.`in`.TodoDto
import com.github.fedorzholud.todolistservice.todo.adapter.`in`.UpdateTodoDto
import com.github.fedorzholud.todolistservice.todo.adapter.out.TodoEntity
import com.github.fedorzholud.todolistservice.todo.application.port.`in`.CreateTodoCommand
import com.github.fedorzholud.todolistservice.todo.application.port.`in`.UpdateTodoCommand
import com.github.fedorzholud.todolistservice.todo.domain.Todo
import com.github.fedorzholud.todolistservice.todo.domain.TodoId

fun CreateTodoDto.toCommand(): CreateTodoCommand =
    CreateTodoCommand(
        description = this.description,
        dueDatetime = this.dueDatetime
    )

fun UpdateTodoDto.toCommand(todoId: TodoId): UpdateTodoCommand =
    UpdateTodoCommand(
        id = todoId,
        description = this.description,
        status = this.status,
    )

fun Todo.toDto(): TodoDto =
    TodoDto(
        id = this.id.value,
        description = this.description,
        status = this.status.value,
        dueDatetime = this.dueDatetime,
        doneDatetime = this.doneDatetime,
        creationDatetime = this.creationDatetime,
    )

fun TodoEntity.toDomain(): Todo =
    Todo(
        id = TodoId(this.id),
        description = this.description,
        status = this.status,
        dueDatetime = this.dueDatetime,
        doneDatetime = this.doneDatetime,
        creationDatetime = this.creationDatetime
    )

fun Todo.toEntity(): TodoEntity =
    TodoEntity(
        id = this.id.value,
        description = this.description,
        status = this.status,
        dueDatetime = this.dueDatetime,
        doneDatetime = this.doneDatetime,
        creationDatetime = this.creationDatetime,
    )