package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.dtos.auth.AuthResponse
import net.bscs22.schoolportal.dtos.auth.LoginRequest
import net.bscs22.schoolportal.services.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class LoginController(private val authService: AuthService) {

    data class ApiResponse<T>(val message: String, val data: T? = null)

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val authResult = authService.authenticate(request)

        return if (authResult != null) {
            ResponseEntity.ok(ApiResponse("Login Successful", authResult))
        } else {
            ResponseEntity.status(401).body(ApiResponse("Invalid credentials"))
        }
    }
}