package com.github.fedorzholud.todolistservice.todo.adapter.out

import org.springframework.data.repository.Repository
import java.util.UUID

interface TodoJpaRepository : Repository<TodoEntity, UUID> {
    fun save(todoEntity: TodoEntity)
}