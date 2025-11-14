package com.github.fedorzholud.todolistservice.todo.adapter.`in`

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fedorzholud.todolistservice.createTestCreateTodoCommand
import com.github.fedorzholud.todolistservice.todo.application.port.`in`.TodoFacade
import com.github.fedorzholud.todolistservice.todo.domain.TodoId
import com.github.fedorzholud.todolistservice.todo.domain.TodoStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TodoIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var todoFacade: TodoFacade

    @Test
    fun `POST api-todos should create todo and return 201 with Location header`() {
        // Arrange
        val dto = CreateTodoDto(
            description = "Dummy description",
            dueDatetime = OffsetDateTime.now().plusDays(3)
        )

        // Act & Assert
        val result = mockMvc.perform(
            post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
            .andReturn()

        val location = result.response.getHeader("Location")
        assertThat(location).isNotNull()

        // Location should look like: /api/todos/{uuid}
        val idPart = location!!.substringAfterLast("/")
        val createdId = UUID.fromString(idPart)
        val todoId = TodoId(createdId)
        val todo = todoFacade.todoById(todoId)

        assertThat(todo.id).isEqualTo(todoId)
        assertThat(todo.description).isEqualTo(dto.description)
        assertThat(todo.dueDatetime).isEqualTo(dto.dueDatetime)
        assertThat(todo.status).isEqualTo(TodoStatus.NOT_DONE)
        assertThat(todo.creationDatetime.isBefore(OffsetDateTime.now())).isTrue()
    }

    @Test
    fun `GET api-todos-id should return todo by id`() {
        // Arrange
        val command = createTestCreateTodoCommand()
        val todoId = todoFacade.createTodo(command)

        // Act
        val mvcResult = mockMvc.perform(
            get("/api/todos/{id}", todoId.value)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()

        val body = mvcResult.response.contentAsString
        val dto = objectMapper.readValue(body, TodoDto::class.java)

        // Assert
        assertThat(dto.id).isEqualTo(todoId.value)
        assertThat(dto.description).isEqualTo(command.description)
        assertThat(dto.status).isEqualTo(TodoStatus.NOT_DONE.value)
        assertThat(dto.dueDatetime).isEqualTo(command.dueDatetime)
        assertThat(dto.doneDatetime).isNull()
        assertThat(dto.creationDatetime.isBefore(OffsetDateTime.now())).isTrue()
    }

    @Test
    fun `GET api-todos with status should return filtered todos`() {
        // Arrange
        val command1 = createTestCreateTodoCommand()
        val command2 = createTestCreateTodoCommand()

        val todoId1 = todoFacade.createTodo(command1)
        val todoId2 = todoFacade.createTodo(command2)

        // Act
        val mvcResult = mockMvc.perform(
            get("/api/todos")
                .param("status", TodoStatus.NOT_DONE.name)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn()

        val body = mvcResult.response.contentAsString
        val todoDtos: List<TodoDto> = objectMapper.readValue(
            body,
            objectMapper.typeFactory.constructCollectionType(List::class.java, TodoDto::class.java)
        )

        // Assert
        assertThat(todoDtos).isNotEmpty
        assertThat(todoDtos.size).isEqualTo(2)
        assertThat(todoDtos.map { it.id }.toSet()).contains(todoId1.value, todoId2.value)
    }

    @Test
    fun `PATCH api-todos-id should update description and status to DONE`() {
        // Arrange
        val command = createTestCreateTodoCommand()
        val todoId = todoFacade.createTodo(command)
        val todo = todoFacade.todoById(todoId)
        val updateDto = UpdateTodoDto(
            description = "Updated description",
            status = TodoStatus.DONE
        )

        // Act
        val mvcResult = mockMvc.perform(
            patch("/api/todos/{id}", todoId.value)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
        )
            .andExpect(status().isOk)
            .andReturn()

        val body = mvcResult.response.contentAsString
        val updatedDto: TodoDto = objectMapper.readValue(body, TodoDto::class.java)

        // Assert
        assertThat(updatedDto.id).isEqualTo(todoId.value)
        assertThat(updatedDto.description).isEqualTo(updateDto.description)
        assertThat(updatedDto.status).isEqualTo(updateDto.status?.value)
        assertThat(updatedDto.doneDatetime).isNotNull
        assertThat(updatedDto.dueDatetime).isEqualTo(todo.dueDatetime)
        assertThat(updatedDto.creationDatetime).isEqualTo(todo.creationDatetime)
    }
}