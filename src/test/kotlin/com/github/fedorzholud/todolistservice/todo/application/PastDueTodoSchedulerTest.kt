package com.github.fedorzholud.todolistservice.todo.application

import com.github.fedorzholud.todolistservice.createTestTodo
import com.github.fedorzholud.todolistservice.todo.application.port.out.TodoRepository
import com.github.fedorzholud.todolistservice.todo.domain.TodoStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@SpringBootTest
@TestPropertySource(
    properties = [
        "scheduler.todo.past-due.enabled=true",
        "scheduler.todo.past-due.interval-ms=60000"
    ]
)
@Transactional
class PastDueTodoSchedulerTest {

    @Autowired
    private lateinit var todoRepository: TodoRepository

    @Autowired
    private lateinit var pastDueTodoScheduler: PastDueTodoScheduler

    @Test
    fun `should mark NOT_DONE todos with past dueDatetime as PAST_DUE`() {
        val now = OffsetDateTime.now()

        // NOT_DONE & due in the past -> should become PAST_DUE
        val pastNotDone = createTestTodo(
            description = "Past not done",
            status = TodoStatus.NOT_DONE,
            dueDatetime = now.minusDays(1)
        )
        todoRepository.saveTodo(pastNotDone)

        // NOT_DONE & due in the future -> should remain NOT_DONE
        val futureNotDone = createTestTodo(
            description = "Future not done",
            status = TodoStatus.NOT_DONE,
            dueDatetime = now.plusDays(1)
        )
        todoRepository.saveTodo(futureNotDone)

        // DONE & due in the past -> should remain DONE
        val pastDone = createTestTodo(
            description = "Past done",
            status = TodoStatus.DONE,
            dueDatetime = now.minusHours(2),
            doneDatetime = now.minusDays(1)
        )
        todoRepository.saveTodo(pastDone)

        // Act: call scheduler directly (no need to wait for real scheduling)
        pastDueTodoScheduler.markPastDueTodos()

        val updatedPastNotDone = todoRepository.todoById(pastNotDone.id)!!
        val updatedFutureNotDone = todoRepository.todoById(futureNotDone.id)!!
        val updatedPastDone = todoRepository.todoById(pastDone.id)!!

        // Assert
        assertThat(updatedPastNotDone.status).isEqualTo(TodoStatus.PAST_DUE)
        assertThat(updatedFutureNotDone.status).isEqualTo(TodoStatus.NOT_DONE)
        assertThat(updatedPastDone.status).isEqualTo(TodoStatus.DONE)
    }
}