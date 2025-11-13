package com.github.fedorzholud.todolistservice.todo.application.port.out

import com.github.fedorzholud.todolistservice.todo.domain.Todo

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
}