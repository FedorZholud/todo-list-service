package com.github.fedorzholud.todolistservice.todo.application

import com.github.fedorzholud.todolistservice.createTestCreateTodoCommand
import com.github.fedorzholud.todolistservice.createTestTodo
import com.github.fedorzholud.todolistservice.todo.application.port.`in`.UpdateTodoCommand
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
        every { todoRepository.saveTodo(capture(todoSlot)) } answers { todoSlot.captured }

        // Act
        val todoId = todoService.createTodo(command)

        // Assert
        val savedTodo = todoSlot.captured

        assertThat(savedTodo.description).isEqualTo(command.description)
        assertThat(savedTodo.dueDatetime).isEqualTo(command.dueDatetime)
        assertThat(savedTodo.status).isEqualTo(TodoStatus.NOT_DONE)
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

    @Test
    fun `should update description only when status is not provided`() {
        // Arrange
        val todo = createTestTodo()

        every { todoRepository.todoById(todo.id) } returns todo
        every { todoRepository.saveTodo(any()) } answers { firstArg() }

        val command = UpdateTodoCommand(
            id = todo.id,
            description = "New description",
            status = null
        )

        // Act
        val updatedTodo = todoService.updateTodo(command)

        // Assert
        assertThat(updatedTodo.id).isEqualTo(todo.id)
        assertThat(updatedTodo.description).isEqualTo(command.description)
        assertThat(updatedTodo.status).isEqualTo(TodoStatus.NOT_DONE)
        assertThat(updatedTodo.doneDatetime).isEqualTo(todo.doneDatetime)

        verify(exactly = 1) { todoRepository.todoById(todo.id) }
        verify(exactly = 1) { todoRepository.saveTodo(any()) }
    }

    @Test
    fun `should set doneDatetime when status changes from NOT_DONE to DONE`() {
        // Arrange
        val todo = createTestTodo()

        every { todoRepository.todoById(todo.id) } returns todo
        every { todoRepository.saveTodo(any()) } answers { firstArg() }

        val command = UpdateTodoCommand(
            id = todo.id,
            description = null,
            status = TodoStatus.DONE
        )

        // Act
        val updatedTodo = todoService.updateTodo(command)

        // Assert
        assertThat(updatedTodo.status).isEqualTo(TodoStatus.DONE)
        assertThat(updatedTodo.doneDatetime).isNotNull()

        verify(exactly = 1) { todoRepository.todoById(todo.id) }
        verify(exactly = 1) { todoRepository.saveTodo(any()) }
    }

    @Test
    fun `should clear doneDatetime when status changes from DONE to NOT_DONE`() {
        // Arrange
        val todo = createTestTodo(status = TodoStatus.DONE)

        every { todoRepository.todoById(todo.id) } returns todo
        every { todoRepository.saveTodo(any()) } answers { firstArg() }

        val command = UpdateTodoCommand(
            id = todo.id,
            description = null,
            status = TodoStatus.NOT_DONE
        )

        // Act
        val updatedTodo = todoService.updateTodo(command)

        // Assert
        assertThat(updatedTodo.status).isEqualTo(TodoStatus.NOT_DONE)
        assertThat(updatedTodo.doneDatetime).isNull()

        verify(exactly = 1) { todoRepository.todoById(todo.id) }
        verify(exactly = 1) { todoRepository.saveTodo(any()) }
    }

    @Test
    fun `should keep doneDatetime unchanged when status stays DONE`() {
        // Arrange
        val todo = createTestTodo(status = TodoStatus.DONE)

        every { todoRepository.todoById(todo.id) } returns todo
        every { todoRepository.saveTodo(any()) } answers { firstArg() }

        val command = UpdateTodoCommand(
            id = todo.id,
            description = "Another desc",
            status = TodoStatus.DONE
        )

        // Act
        val updatedTodo = todoService.updateTodo(command)

        // Assert
        assertThat(updatedTodo.status).isEqualTo(TodoStatus.DONE)
        assertThat(updatedTodo.doneDatetime).isEqualTo(todo.doneDatetime)

        verify(exactly = 1) { todoRepository.todoById(todo.id) }
        verify(exactly = 1) { todoRepository.saveTodo(any()) }
    }

    @Test
    fun `should throw UpdateTodoStatusToPastDueForbiddenException when trying to set status to PAST_DUE explicitly`() {
        // Arrange
        val todo = createTestTodo()

        every { todoRepository.todoById(todo.id) } returns todo

        val command = UpdateTodoCommand(
            id = todo.id,
            description = null,
            status = TodoStatus.PAST_DUE
        )

        // Act & Assert
        assertThatExceptionOfType(UpdateTodoStatusToPastDueForbiddenException::class.java).isThrownBy {
            todoService.updateTodo(command)
        }

        verify(exactly = 1) { todoRepository.todoById(todo.id) }
        verify(exactly = 0) { todoRepository.saveTodo(any()) }
    }

    @Test
    fun `should throw PastDueTodoModificationForbiddenException when existing todo is already PAST_DUE`() {
        // Arrange
        val todo = createTestTodo(status = TodoStatus.PAST_DUE)

        every { todoRepository.todoById(todo.id) } returns todo

        val command = UpdateTodoCommand(
            id = todo.id,
            description = "New desc",
            status = TodoStatus.DONE
        )

        // Act & Assert
        assertThatExceptionOfType(PastDueTodoModificationForbiddenException::class.java).isThrownBy {
            todoService.updateTodo(command)
        }

        verify(exactly = 1) { todoRepository.todoById(todo.id) }
        verify(exactly = 0) { todoRepository.saveTodo(any()) }
    }

    @Test
    fun `should throw PastDueTodoModificationForbiddenException when due datetime is in past and todo is in status NOT_DONE`() {
        // Arrange
        val todo = createTestTodo(dueDatetime = OffsetDateTime.now().minusDays(1))

        every { todoRepository.todoById(todo.id) } returns todo

        val command = UpdateTodoCommand(
            id = todo.id,
            description = "New desc",
            status = TodoStatus.DONE
        )

        // Act & Assert
        assertThatExceptionOfType(PastDueTodoModificationForbiddenException::class.java).isThrownBy {
            todoService.updateTodo(command)
        }

        verify(exactly = 1) { todoRepository.todoById(todo.id) }
        verify(exactly = 0) { todoRepository.saveTodo(any()) }
    }
}