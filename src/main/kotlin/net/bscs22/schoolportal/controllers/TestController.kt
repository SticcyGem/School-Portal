package net.bscs22.schoolportal.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/test")
class TestController {

    @GetMapping("/public")
    fun publicEndpoint(): ResponseEntity<String> {
        return ResponseEntity.ok("This is a public endpoint. Anyone can see this.")
    }

    @GetMapping("/protected")
    fun protectedEndpoint(principal: Principal): ResponseEntity<String> {
        return ResponseEntity.ok("SUCCESS: You have accessed a protected endpoint! User: ${principal.name}")
    }
}