package com.github.fedorzholud.todolistservice.todo.adapter.out

import com.github.fedorzholud.todolistservice.todo.adapter.toEntity
import com.github.fedorzholud.todolistservice.todo.application.port.out.TodoRepository
import com.github.fedorzholud.todolistservice.todo.domain.Todo
import org.springframework.stereotype.Component

@Component
class TodoPersistenceAdapter(private val todoJpaRepository: TodoJpaRepository) : TodoRepository {
    override fun saveTodo(todo: Todo) {
        val entity: TodoEntity = todo.toEntity()
        todoJpaRepository.save(entity)
    }
}