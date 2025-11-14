package com.github.fedorzholud.todolistservice.todo.application.port.`in`

import com.github.fedorzholud.todolistservice.todo.domain.*
import java.time.OffsetDateTime

/**
 * Application service (facade) that exposes use cases for managing to-do items.
 *
 * This interface represents the entry point into the to-do application core.
 * It defines the available operations for external layers (e.g. REST controllers,
 * messaging adapters, CLI). Implementations execute domain logic and coordinate
 * interactions with repositories and other services.
 *
 * Following the Dependency Inversion Principle (DIP), callers depend on this
 * abstract interface rather than any concrete implementation or infrastructure.
 */
interface TodoFacade {

    /**
     * Creates a new to-do item using the provided command.
     *
     * @param command [CreateTodoCommand] - the data required to create a new to-do item
     * @throws DueDatetimeCouldNotBeInPastException if due datetime is in the past
     */
    fun createTodo(command: CreateTodoCommand): TodoId

    /**
     * Retrieves a to-do item by its unique identifier.
     *
     * @param todoId [TodoId] - the identifier of the to-do to retrieve
     * @return the matching to-do aggregate
     * @throws TodoNotFoundException if no item exists with the given identifier
     */
    fun todoById(todoId: TodoId): Todo

    /**
     * Retrieves to-do items filtered by their status.
     *
     * @param status [TodoStatus] - optional status filter; if null, all Todos are returned
     * @return a set containing all matching Todos
     */
    fun todos(status: TodoStatus?): Set<Todo>

    /**
     * Updates an existing to-do item using the provided [UpdateTodoCommand].
     *
     * This operation applies only non-null fields from the command and enforces
     * all business rules related to to-do modification.
     *
     * Business logic enforced:
     * - A to-do item already in `PAST_DUE` status cannot be modified.
     * - The status cannot be explicitly set to `PAST_DUE`.
     * - `doneDatetime` adjustments:
     *   - `NOT_DONE → DONE`: sets `doneDatetime` to the current timestamp.
     *   - `DONE → NOT_DONE`: clears `doneDatetime`.
     *   - Unchanged status preserves the existing `doneDatetime`.
     *
     * @param command the incoming update request
     * @return the updated to-do item
     *
     * @throws TodoNotFoundException if no to-do item with the given ID exists
     * @throws PastDueTodoModificationForbiddenException if the item is already past due
     * @throws UpdateTodoStatusToPastDueForbiddenException if attempting to set status to `PAST_DUE`
     */
    fun updateTodo(command: UpdateTodoCommand): Todo
}

/**
 * Command object carrying the data required to create a new to-do item.
 *
 * This object is part of the application's input model and represents
 * a request coming from an external layer. It does not contain any
 * domain logic — only the information needed to execute the use case.
 *
 * @property description [String] - the optional text associated with the to-do
 * @property dueDatetime [OffsetDateTime] - the deadline by which the to-do should be done
 */
data class CreateTodoCommand(
    val description: String,
    val dueDatetime: OffsetDateTime,
)

/**
 * Command object representing a request to update an existing to-do item.
 *
 * This command supports partial updates. Each field is optional:
 * if a field is null, the corresponding attribute of the to-do item will remain unchanged.
 *
 * @property id the identifier of the to-do item to update
 * @property description the new description, or null to leave it unchanged
 * @property status the new status, or null to leave it unchanged
 */
data class UpdateTodoCommand(
    val id: TodoId,
    val description: String?,
    val status: TodoStatus?,
)