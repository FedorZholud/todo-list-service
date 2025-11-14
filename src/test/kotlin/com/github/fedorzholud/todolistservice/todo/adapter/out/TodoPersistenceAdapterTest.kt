package com.github.fedorzholud.todolistservice.todo.adapter.out

import com.github.fedorzholud.todolistservice.createTestTodo
import com.github.fedorzholud.todolistservice.todo.domain.TodoStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(TodoPersistenceAdapter::class)
class TodoPersistenceAdapterTest {

    @Autowired
    private lateinit var adapter: TodoPersistenceAdapter

    @Test
    fun `should save and load todo`() {
        // Arrange
        val todo = createTestTodo()

        // Act
        adapter.saveTodo(todo)
        val loaded = adapter.todoById(todo.id)

        // Assert
        assertThat(loaded).isNotNull
        assertThat(loaded).isEqualTo(todo)
    }

    @Test
    fun `should find todos by status`() {
        // Arrange
        val notDone = createTestTodo(status = TodoStatus.NOT_DONE)
        val done = createTestTodo(status = TodoStatus.DONE)

        adapter.saveTodo(notDone)
        adapter.saveTodo(done)

        // Act
        val notDoneTodos = adapter.todos(TodoStatus.NOT_DONE)

        // Assert
        assertThat(notDoneTodos).extracting<TodoStatus> { it.status }
            .containsOnly(TodoStatus.NOT_DONE)
    }
}