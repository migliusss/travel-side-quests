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

    /**
     * The inherited handler already answers 400, but without saying which fields failed —
     * which is the only part a submission form can act on.
     */
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
            title = "Invalid request"
            detail = "The submission has ${ex.bindingResult.errorCount} invalid field(s)."
            setProperty(
                "errors",
                ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "is invalid") }
            )
        }

        return ResponseEntity.badRequest().body(problem)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ProblemDetail> =
        problem(HttpStatus.NOT_FOUND, "Not Found", ex.message ?: "Not found.")

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException): ResponseEntity<ProblemDetail> =
        problem(HttpStatus.BAD_REQUEST, "Invalid request", ex.message ?: "Invalid request.")

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleConflict(ex: DataIntegrityViolationException): ResponseEntity<ProblemDetail> {
        // Two submitters naming the same new destination at the same moment land here.
        log.warn("constraint violation while writing.", ex)

        return problem(
            HttpStatus.CONFLICT,
            "Conflict",
            "That conflicts with something already saved. Try again."
        )
    }

    @ExceptionHandler(TooManyRequestsException::class)
    fun handleTooManyRequests(ex: TooManyRequestsException): ResponseEntity<ProblemDetail> =
        problem(HttpStatus.TOO_MANY_REQUESTS, "Too many requests", ex.message ?: "Slow down.")

    // MaxUploadSizeExceededException (413), HttpMessageNotReadableException (400) and
    // type mismatches are already mapped to ProblemDetail by the inherited handlers.

    @ExceptionHandler
    fun handleUnexpectedException(ex: Exception): ResponseEntity<ProblemDetail> {
        log.error("unexpected exception occurred.", ex)

        return problem(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "Unexpected error occurred. Try again later."
        )
    }

    private fun problem(
        status: HttpStatus,
        title: String,
        detail: String
    ): ResponseEntity<ProblemDetail> {
        val problem = ProblemDetail.forStatusAndDetail(status, detail).apply {
            this.title = title
        }

        return ResponseEntity.status(status).body(problem)
    }
}
