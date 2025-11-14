package com.github.fedorzholud.todolistservice.todo.adapter.out

import com.github.fedorzholud.todolistservice.todo.adapter.toDomain
import com.github.fedorzholud.todolistservice.todo.adapter.toEntity
import com.github.fedorzholud.todolistservice.todo.application.port.out.TodoRepository
import com.github.fedorzholud.todolistservice.todo.domain.Todo
import com.github.fedorzholud.todolistservice.todo.domain.TodoId
import com.github.fedorzholud.todolistservice.todo.domain.TodoStatus
import org.springframework.stereotype.Component

@Component
class TodoPersistenceAdapter(private val todoJpaRepository: TodoJpaRepository) : TodoRepository {

    override fun saveTodo(todo: Todo) {
        val entity: TodoEntity = todo.toEntity()
        todoJpaRepository.save(entity)
    }

    override fun todoById(todoId: TodoId): Todo? =
        todoJpaRepository.findById(todoId.value)?.toDomain()

    override fun todos(status: TodoStatus?): Set<Todo> {
        val entities = when (status) {
            null -> todoJpaRepository.findAll()
            else -> todoJpaRepository.findAllByStatus(status)
        }

        return entities.map { it.toDomain() }.toSet()
    }
}