package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.dtos.auth.AuthResponse // Import the new DTO
import net.bscs22.schoolportal.dtos.auth.LoginRequest // Import the new DTO
import net.bscs22.schoolportal.services.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class LoginController(private val authService: AuthService) {

    // Helper wrapper for consistent JSON responses
    data class ApiResponse<T>(val message: String, val data: T? = null)

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        // FIX: Pass the WHOLE request object, not separate fields
        val authResult = authService.authenticate(request)

        return if (authResult != null) {
            ResponseEntity.ok(ApiResponse("Login Successful", authResult))
        } else {
            ResponseEntity.status(401).body(ApiResponse("Invalid credentials"))
        }
    }
}