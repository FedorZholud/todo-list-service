package com.github.fedorzholud.todolistservice.todo.adapter.out

import com.github.fedorzholud.todolistservice.todo.domain.TodoStatus
import org.springframework.data.repository.Repository
import java.time.OffsetDateTime
import java.util.UUID

interface TodoJpaRepository : Repository<TodoEntity, UUID> {
    fun save(todoEntity: TodoEntity): TodoEntity

    fun findById(id: UUID): TodoEntity?

    fun findAll(): Iterable<TodoEntity>

    fun findAllByStatus(status: TodoStatus): Iterable<TodoEntity>

    fun findAllByStatusAndDueDatetimeBefore(status: TodoStatus, dueDatetimeBefore: OffsetDateTime): Iterable<TodoEntity>
}