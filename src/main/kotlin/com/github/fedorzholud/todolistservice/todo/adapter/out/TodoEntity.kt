package com.github.fedorzholud.todolistservice.todo.adapter.out

import com.github.fedorzholud.todolistservice.todo.domain.TodoStatus
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "todos")
class TodoEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID,

    @Column(name = "description")
    val description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: TodoStatus,

    @Column(name = "due_datetime", nullable = false)
    val dueDatetime: OffsetDateTime,

    @Column(name = "done_datetime")
    val doneDatetime: OffsetDateTime? = null,

    @Column(name = "creation_datetime", nullable = false)
    val creationDatetime: OffsetDateTime = OffsetDateTime.now()
)