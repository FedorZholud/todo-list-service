package com.github.fedorzholud.todolistservice.todo.application.port.`in`

import com.github.fedorzholud.todolistservice.todo.domain.TodoId
import java.time.OffsetDateTime

/**
 * Application service (facade) that exposes use cases for managing Todo items.
 *
 * This interface represents the entry point into the Todo application core.
 * It defines the available operations for external layers (e.g. REST controllers,
 * messaging adapters, CLI). Implementations execute domain logic and coordinate
 * interactions with repositories and other services.
 *
 * Following the Dependency Inversion Principle (DIP), callers depend on this
 * abstract interface rather than any concrete implementation or infrastructure.
 */
interface TodoFacade {

    /**
     * Creates a new Todo item using the provided command.
     *
     * @param command [CreateTodoCommand] - the data required to create a new Todo item
     */
    fun createTodo(command: CreateTodoCommand): TodoId
}

/**
 * Command object carrying the data required to create a new Todo item.
 *
 * This object is part of the application's input model and represents
 * a request coming from an external layer. It does not contain any
 * domain logic — only the information needed to execute the use case.
 *
 * @property description [String] - the optional text associated with the Todo
 * @property dueDatetime [OffsetDateTime] - the deadline by which the Todo should be done
 */
data class CreateTodoCommand(
    val description: String,
    val dueDatetime: OffsetDateTime,
)