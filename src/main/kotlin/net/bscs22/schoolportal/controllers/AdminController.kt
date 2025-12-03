package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.services.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
class AdminController(private val authService: AuthService) {
    data class RegisterAdminRequest(
        val email: String,
        val password: String,
        val firstName: String,
        val lastName: String
    )

    @PostMapping("/register")
    fun registerAdmin(@RequestBody request: RegisterAdminRequest): ResponseEntity<Any> {
        return try {
            val resultMessage = authService.registerAdmin(
                request.email,
                request.password,
                request.firstName,
                request.lastName
            )
            ResponseEntity.ok(mapOf("message" to resultMessage))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
}