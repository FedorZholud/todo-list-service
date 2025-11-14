package com.github.fedorzholud.todolistservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TodoListServiceApplication

fun main(args: Array<String>) {
    runApplication<TodoListServiceApplication>(*args)
}
