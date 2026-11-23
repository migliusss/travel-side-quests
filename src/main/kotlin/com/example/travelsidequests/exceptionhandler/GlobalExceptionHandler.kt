package com.example.travelsidequests.exceptionhandler

import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ProblemDetail> =
        problem(
            HttpStatus.NOT_FOUND,
            "Not Found",
            ex.message ?: "Not found.")

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException): ResponseEntity<ProblemDetail> =
        problem(
            HttpStatus.BAD_REQUEST,
            "Invalid request",
            ex.message ?: "Invalid request.")

    @ExceptionHandler(TooManyRequestsException::class)
    fun handleTooManyRequests(ex: TooManyRequestsException): ResponseEntity<ProblemDetail> =
        problem(
            HttpStatus.TOO_MANY_REQUESTS,
            "Too many requests",
            ex.message ?: "Slow down.")

    @ExceptionHandler
    fun handleUnexpectedException(ex: Exception): ResponseEntity<ProblemDetail> {
        log.error("unexpected exception occurred.", ex)

        return problem(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "Unexpected error occurred. Try again later."
        )
    }

    private fun problem(status: HttpStatus, title: String, detail: String): ResponseEntity<ProblemDetail> {
        val problem = ProblemDetail.forStatusAndDetail(status, detail).apply {
            this.title = title
        }

        return ResponseEntity.status(status).body(problem)
    }
}
