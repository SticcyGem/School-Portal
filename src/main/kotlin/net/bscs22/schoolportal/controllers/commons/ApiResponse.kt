package net.bscs22.schoolportal.common

import java.time.LocalDateTime

data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun <T> success(data: T, message: String? = null): ApiResponse<T> {
            return ApiResponse(success = true, message = message, data = data)
        }
        fun success(message: String): ApiResponse<Nothing> {
            return ApiResponse(success = true, message = message)
        }
        fun error(message: String): ApiResponse<Nothing> {
            return ApiResponse(success = false, message = message)
        }
    }
}