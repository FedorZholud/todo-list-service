package com.github.fedorzholud.todolistservice.todo.adapter.`in`

import com.github.fedorzholud.todolistservice.todo.adapter.toCommand
import com.github.fedorzholud.todolistservice.todo.adapter.toDto
import com.github.fedorzholud.todolistservice.todo.application.port.`in`.CreateTodoCommand
import com.github.fedorzholud.todolistservice.todo.application.port.`in`.TodoFacade
import com.github.fedorzholud.todolistservice.todo.domain.TodoId
import com.github.fedorzholud.todolistservice.todo.domain.TodoStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/api/todos")
class TodoController(private val todoFacade: TodoFacade) {

    @PostMapping
    fun createTodo(@RequestBody createTodoDto: CreateTodoDto): ResponseEntity<Void> {
        val command: CreateTodoCommand = createTodoDto.toCommand()
        val todoId: TodoId = todoFacade.createTodo(command)
        val location: URI = URI.create("/api/todos/${todoId.value}")
        return ResponseEntity.created(location).build()
    }

    @GetMapping("/{id}")
    fun todoById(@PathVariable(value = "id", required = true) id: UUID): TodoDto =
        todoFacade.todoById(TodoId(id)).toDto()

    @GetMapping
    fun todos(@RequestParam(value = "status") status: TodoStatus?): List<TodoDto> =
        todoFacade.todos(status).map { it.toDto() }
}