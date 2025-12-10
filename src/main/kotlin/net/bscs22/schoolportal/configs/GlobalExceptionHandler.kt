package net.bscs22.schoolportal.configs

import net.bscs22.schoolportal.common.ApiResponse
import org.hibernate.exception.GenericJDBCException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.sql.SQLException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val errors = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("Validation failed: $errors"))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ex.message ?: "Invalid request"))
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleConflict(ex: IllegalStateException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.message ?: "Action conflict"))
    }

    @ExceptionHandler(BadCredentialsException::class, UsernameNotFoundException::class)
    fun handleAuthError(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        ex.printStackTrace()
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("Invalid email or password"))
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDbViolation(ex: DataIntegrityViolationException): ResponseEntity<ApiResponse<Nothing>> {
        ex.printStackTrace()
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error("Cannot proceed: This record is currently in use or creates a duplicate."))
    }

    @ExceptionHandler(GenericJDBCException::class)
    fun handleSqlTriggerError(ex: GenericJDBCException): ResponseEntity<ApiResponse<Nothing>> {
        val sqlEx = ex.cause as? SQLException
        val message = sqlEx?.message ?: "Database error occurred"
        if (message.contains("Grade Component Violation") ||
            message.contains("Schedule Conflict") ||
            message.contains("Score Violation")) {
            val cleanMessage = message.substringBefore("\n").removePrefix("ERROR: ")
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(cleanMessage))
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Database error"))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        ex.printStackTrace()
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("An unexpected internal error occurred."))
    }
}