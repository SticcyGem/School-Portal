package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.common.ApiResponse
import net.bscs22.schoolportal.dtos.auth.AuthResponse
import net.bscs22.schoolportal.dtos.auth.LoginRequest
import net.bscs22.schoolportal.services.AuthenticationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class LoginController(private val authenticationService: AuthenticationService) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val authResult = authenticationService.authenticate(request)
        return ResponseEntity.ok(ApiResponse.success(authResult, "Login Successful"))
    }
}