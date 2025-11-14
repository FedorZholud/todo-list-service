package com.github.fedorzholud.todolistservice.todo.application

import com.github.fedorzholud.todolistservice.todo.application.port.out.TodoRepository
import com.github.fedorzholud.todolistservice.todo.domain.TodoStatus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
@Transactional
class PastDueTodoScheduler(
    private val todoRepository: TodoRepository,
    @Value("\${scheduler.todo.past-due.enabled:true}")
    private val enabled: Boolean
) {

    /**
     * Executes a periodic check for overdue to-do items.
     *
     * The execution interval is configured via the `fixedDelayString` expression
     * in the [Scheduled] annotation.
     *
     * If the scheduler is disabled through the `enabled`
     * configuration property, the method exits without performing any work.
     *
     * Steps performed:
     * - Determines the current timestamp.
     * - Retrieves all to-do items with status [TodoStatus.NOT_DONE] whose `dueDatetime`
     *   is strictly before the current time.
     * - Updates each of these items by setting their status to [TodoStatus.PAST_DUE].
     *
     * This method runs within a transactional boundary to ensure all overdue
     * updates are applied atomically.
     */
    @Scheduled(fixedDelayString = "\${scheduler.todo.past-due.interval-ms}")
    fun markPastDueTodos() {
        if (!enabled) {
            log.warn("Scheduler is disabled")
            return
        }

        val now = OffsetDateTime.now()
        log.info("Starting past-due check at {}", now)

        val notDoneTodos = todoRepository.todosByStatusAndDueDatetimeBefore(TodoStatus.NOT_DONE, now)
        log.info("Found ${notDoneTodos.size} NOT_DONE todos to mark as PAST_DUE")

        notDoneTodos.forEach { todo ->
            val pastDueTodo = todo.copy(status = TodoStatus.PAST_DUE)
            todoRepository.saveTodo(pastDueTodo)
            log.debug("Marked todo {} as {}", todo.id.value, TodoStatus.PAST_DUE)
        }

        log.info("Finished past-due check at {}", OffsetDateTime.now())
    }

    companion object {
        private val log = LoggerFactory.getLogger(PastDueTodoScheduler::class.java)
    }
}