package com.github.fedorzholud.todolistservice.todo.application

import com.github.fedorzholud.todolistservice.todo.application.port.`in`.CreateTodoCommand
import com.github.fedorzholud.todolistservice.todo.application.port.`in`.TodoFacade
import com.github.fedorzholud.todolistservice.todo.application.port.`in`.UpdateTodoCommand
import com.github.fedorzholud.todolistservice.todo.application.port.out.TodoRepository
import com.github.fedorzholud.todolistservice.todo.domain.*
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

        return todoRepository.saveTodo(todo).id
    }

    override fun todoById(todoId: TodoId): Todo =
        todoRepository.todoById(todoId) ?: throw TodoNotFoundException(
            todoId = todoId,
            message = "Todo with id: ${todoId.value} could not be found"
        )

    override fun todos(status: TodoStatus?): Set<Todo> =
        todoRepository.todos(status)

    override fun updateTodo(command: UpdateTodoCommand): Todo {
        val todo = todoById(command.id)

        enforceNotPastDue(todo)

        if (command.status == TodoStatus.PAST_DUE) {
            throw UpdateTodoStatusToPastDueForbiddenException(
                todoId = todo.id,
                message = "Status update to ${command.status.value} value for todo with id ${todo.id.value} is forbidden"
            )
        }

        val newDoneDatetime = when {
            // NOT_DONE -> DONE : set done time
            todo.status == TodoStatus.NOT_DONE && command.status == TodoStatus.DONE -> OffsetDateTime.now()

            // DONE -> NOT_DONE : clear done time
            todo.status == TodoStatus.DONE && command.status == TodoStatus.NOT_DONE -> null

            // any other case (including status null or unchanged)
            else -> todo.doneDatetime
        }

        val updatedTodo = todo.copy(
            description = command.description ?: todo.description,
            status = command.status ?: todo.status,
            doneDatetime = newDoneDatetime
        )

        return todoRepository.saveTodo(updatedTodo)
    }

    private fun enforceNotPastDue(todo: Todo) {
        val now = OffsetDateTime.now()
        val isPastDue = todo.status == TodoStatus.PAST_DUE
                || (todo.status == TodoStatus.NOT_DONE && todo.dueDatetime.isBefore(now))

        if (isPastDue) {
            throw PastDueTodoModificationForbiddenException(
                todoId = todo.id,
                message = "Todo with id ${todo.id.value} is in the status ${TodoStatus.PAST_DUE.value} and could not be modified"
            )
        }
    }
}