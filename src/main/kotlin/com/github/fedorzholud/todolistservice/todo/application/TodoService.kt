package com.github.fedorzholud.todolistservice.todo.application

import com.github.fedorzholud.todolistservice.todo.application.port.`in`.CreateTodoCommand
import com.github.fedorzholud.todolistservice.todo.application.port.`in`.TodoFacade
import com.github.fedorzholud.todolistservice.todo.application.port.out.TodoRepository
import com.github.fedorzholud.todolistservice.todo.domain.DueDatetimeCouldNotBeInPastException
import com.github.fedorzholud.todolistservice.todo.domain.Todo
import com.github.fedorzholud.todolistservice.todo.domain.TodoId
import com.github.fedorzholud.todolistservice.todo.domain.TodoStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
@Transactional
class TodoService(private val todoRepository: TodoRepository) : TodoFacade {
    override fun createTodo(command: CreateTodoCommand): TodoId {
        val now = OffsetDateTime.now()

        if (command.dueDatetime.isBefore(now)) {
            throw DueDatetimeCouldNotBeInPastException(
                dueDatetime = command.dueDatetime,
                message = "Due datetime ${command.dueDatetime} could not be in the past"
            )
        }

        val todo = Todo(
            description = command.description,
            status = TodoStatus.NOT_DONE,
            dueDatetime = command.dueDatetime
        )

        todoRepository.saveTodo(todo)
        return todo.id
    }
}