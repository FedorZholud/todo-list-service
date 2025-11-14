package com.github.fedorzholud.todolistservice.todo.application

import com.github.fedorzholud.todolistservice.createTestCreateTodoCommand
import com.github.fedorzholud.todolistservice.createTestTodo
import com.github.fedorzholud.todolistservice.todo.application.port.out.TodoRepository
import com.github.fedorzholud.todolistservice.todo.domain.*
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class TodoServiceTest {

    private val todoRepository: TodoRepository = mockk()
    private val todoService: TodoService = TodoService(todoRepository)

    @Test
    fun `should create a new todo item`() {
        // Arrange
        val command = createTestCreateTodoCommand()
        val todoSlot = slot<Todo>()
        every { todoRepository.saveTodo(capture(todoSlot)) } just Runs

        // Act
        val todoId = todoService.createTodo(command)

        // Assert
        val savedTodo = todoSlot.captured

        assertThat(command.description).isEqualTo(savedTodo.description)
        assertThat(command.dueDatetime).isEqualTo(savedTodo.dueDatetime)
        assertThat(TodoStatus.NOT_DONE).isEqualTo(savedTodo.status)
        assertThat(savedTodo.id).isEqualTo(todoId)
        assertThat(savedTodo.creationDatetime.isBefore(OffsetDateTime.now())).isTrue()
    }

    @Test
    fun `should throw DueDatetimeCouldNotBeInPastException when due datetime is in the past`() {
        // Arrange
        val command = createTestCreateTodoCommand(dueDatetime = OffsetDateTime.now().minusDays(3))

        // Act & Assert
        assertThatExceptionOfType(DueDatetimeCouldNotBeInPastException::class.java).isThrownBy {
            todoService.createTodo(command)
        }

        verify(exactly = 0) { todoRepository.saveTodo(any()) }
    }

    @Test
    fun `should return existing todo by given id`() {
        // Arrange
        val todoId = TodoId()
        val expectedTodo = createTestTodo(id = todoId)

        every { todoRepository.todoById(todoId) } returns expectedTodo

        // Act
        val result = todoService.todoById(todoId)

        // Assert
        assertThat(result).isEqualTo(expectedTodo)
        verify(exactly = 1) { todoRepository.todoById(todoId) }
        confirmVerified(todoRepository)
    }

    @Test
    fun `should throw TodoNotFoundException when todo by given id does not exist`() {
        // Arrange
        val todoId = TodoId()
        every { todoRepository.todoById(todoId) } returns null

        // Act & Assert
        assertThatExceptionOfType(TodoNotFoundException::class.java).isThrownBy {
            todoService.todoById(todoId)
        }

        verify(exactly = 1) { todoRepository.todoById(todoId) }
        confirmVerified(todoRepository)
    }

    @Test
    fun `should return all todos when status is null`() {
        // Arrange
        val todo1 = createTestTodo()
        val todo2 = createTestTodo(status = TodoStatus.DONE)

        every { todoRepository.todos(null) } returns setOf(todo1, todo2)

        // Act
        val result = todoService.todos(null)

        // Assert
        assertThat(result).containsExactlyInAnyOrder(todo1, todo2)
        verify(exactly = 1) { todoRepository.todos(null) }
        confirmVerified(todoRepository)
    }

    @Test
    fun `should return todos filtered by given status`() {
        // Arrange
        val status = TodoStatus.NOT_DONE
        val todo1 = createTestTodo(status = status)

        every { todoRepository.todos(status) } returns setOf(todo1)

        // Act
        val result = todoService.todos(status)

        // Assert
        assertThat(result).containsExactly(todo1)
        verify(exactly = 1) { todoRepository.todos(status) }
        confirmVerified(todoRepository)
    }
}