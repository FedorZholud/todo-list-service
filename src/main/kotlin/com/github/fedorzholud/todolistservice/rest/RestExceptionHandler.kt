package com.github.fedorzholud.todolistservice.rest

import com.github.fedorzholud.todolistservice.todo.domain.DueDatetimeCouldNotBeInPastException
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
}