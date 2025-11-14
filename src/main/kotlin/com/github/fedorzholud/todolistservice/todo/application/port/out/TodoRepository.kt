package com.github.fedorzholud.todolistservice.todo.application.port.out

import com.github.fedorzholud.todolistservice.todo.domain.Todo
import com.github.fedorzholud.todolistservice.todo.domain.TodoId
import com.github.fedorzholud.todolistservice.todo.domain.TodoStatus

/**
 * Repository port for accessing and modifying Todo aggregates.
 *
 * This interface defines persistence operations that the domain layer
 * requires, without exposing any infrastructure details such as JPA,
 * SQL, or data sources. Implementations of this interface are provided
 * by adapters in the infrastructure layer.
 */
interface TodoRepository {

    /**
     * Persists the given Todo aggregate.
     * If the Todo does not yet exist in storage, it will be created.
     * If it already exists (identified by its id), it will be updated.
     *
     * @param todo [Todo] - the Todo aggregate to persist
     */
    fun saveTodo(todo: Todo)

    /**
     * Retrieves a single Todo aggregate by its identifier.
     *
     * @param todoId [TodoId] - the identifier of the Todo to receive
     * @return the Todo if found, or `null` if no Todo exists with the given id
     */
    fun todoById(todoId: TodoId): Todo?

    /**
     * Retrieves all Todo aggregates, optionally filtered by their current status.
     *
     * @param status [TodoStatus] - optional status filter; if null, all Todos are returned
     * @return a set containing all matching Todos
     */
    fun todos(status: TodoStatus?): Set<Todo>
}