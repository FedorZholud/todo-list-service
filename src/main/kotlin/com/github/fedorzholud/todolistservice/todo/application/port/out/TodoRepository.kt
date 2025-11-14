package com.github.fedorzholud.todolistservice.todo.application.port.out

import com.github.fedorzholud.todolistservice.todo.domain.Todo
import com.github.fedorzholud.todolistservice.todo.domain.TodoId
import com.github.fedorzholud.todolistservice.todo.domain.TodoStatus
import java.time.OffsetDateTime

/**
 * Repository port for accessing and modifying to-do aggregates.
 *
 * This interface defines persistence operations that the domain layer
 * requires, without exposing any infrastructure details such as JPA,
 * SQL, or data sources. Implementations of this interface are provided
 * by adapters in the infrastructure layer.
 */
interface TodoRepository {

    /**
     * Persists the given to-do aggregate.
     * If the to-do does not yet exist in storage, it will be created.
     * If it already exists (identified by its id), it will be updated.
     *
     * @param todo [Todo] - the to-do aggregate to persist
     * @return created to-do
     */
    fun saveTodo(todo: Todo): Todo

    /**
     * Retrieves a single to-do aggregate by its identifier.
     *
     * @param todoId [TodoId] - the identifier of the to-do to receive
     * @return the to-do if found, or `null` if no to-do exists with the given id
     */
    fun todoById(todoId: TodoId): Todo?

    /**
     * Retrieves all to-do aggregates, optionally filtered by their current status.
     *
     * @param status [TodoStatus] - optional status filter; if null, all Todos are returned
     * @return a set containing all matching Todos
     */
    fun todos(status: TodoStatus?): Set<Todo>

    /**
     * Retrieves all to-do aggregates that match the given status and whose `dueDatetime`
     * lies strictly before the specified timestamp.
     *
     * @param status the status that returned to-dos must currently have (e.g., [TodoStatus.NOT_DONE])
     * @param timeBefore the upper bound on the `dueDatetime`; only items with a dueDatetime earlier than this value are returned
     * @return a set of all to-dos whose status matches the given value and whose dueDatetime is before the specified timestamp
     */
    fun todosByStatusAndDueDatetimeBefore(status: TodoStatus, timeBefore: OffsetDateTime): Set<Todo>
}