package com.github.fedorzholud.todolistservice.rest

import com.github.fedorzholud.todolistservice.todo.domain.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@Suppress("unused")
@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(DueDatetimeCouldNotBeInPastException::class)
    private fun handleDueDatetimeCouldNotBeInPastException(exception: DueDatetimeCouldNotBeInPastException): ResponseEntity<ErrorDto> =
        ResponseEntity(
            ErrorDto(
                message = exception.message ?: "Due datetime ${exception.dueDatetime} could not be in the past"
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        )

    @ExceptionHandler(TodoNotFoundException::class)
    private fun handleTodoNotFoundException(exception: TodoNotFoundException): ResponseEntity<ErrorDto> =
        ResponseEntity(
            ErrorDto(
                message = exception.message ?: "Todo with id: ${exception.todoId.value} could not be found"
            ),
            HttpStatus.NOT_FOUND
        )

    @ExceptionHandler(PastDueTodoModificationForbiddenException::class)
    private fun handlePastDueTodoModificationForbiddenException(exception: PastDueTodoModificationForbiddenException): ResponseEntity<ErrorDto> =
        ResponseEntity(
            ErrorDto(
                message = exception.message
                    ?: "Todo with id ${exception.todoId.value} is in the status ${TodoStatus.PAST_DUE.value} and could not be modified"
            ),
            HttpStatus.FORBIDDEN
        )

    @ExceptionHandler(UpdateTodoStatusToPastDueForbiddenException::class)
    private fun handleUpdateTodoStatusToPastDueForbiddenException(exception: UpdateTodoStatusToPastDueForbiddenException): ResponseEntity<ErrorDto> =
        ResponseEntity(
            ErrorDto(
                message = exception.message
                    ?: "Status update to ${TodoStatus.PAST_DUE.value} value for todo with id ${exception.todoId.value} is forbidden"
            ),
            HttpStatus.FORBIDDEN
        )
}