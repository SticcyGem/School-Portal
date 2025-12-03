package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.services.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class LoginController(private val authService: AuthService) {

    data class LoginRequest(val email: String, val password: String)
    data class LoginResponse(val message: String, val payload: Map<String, Any>? = null)

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val authResult = authService.authenticate(
            request.email,
            request.password)
        return if (authResult != null) {
            ResponseEntity.ok(LoginResponse("Login Successful", authResult))
        } else {
            ResponseEntity.status(401).body(LoginResponse("Invalid credentials"))
        }
    }
}