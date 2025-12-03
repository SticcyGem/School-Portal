package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.services.AccountService
import net.bscs22.schoolportal.services.JwtService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/account")
class AccountController(
    private val accountService: AccountService,
    private val jwtService: JwtService
) {

    data class ChangePasswordRequest(val oldPass: String, val newPass: String)

    @PostMapping("/change-password")
    fun changePassword(
        @RequestHeader("Authorization") tokenHeader: String,
        @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<Any> {
        return try {
            val token = tokenHeader.substring(7)
            val accountIdStr = jwtService.extractClaim(token) { claims -> claims["accountId"] as String }
            val accountId = java.util.UUID.fromString(accountIdStr)

            val message = accountService.changePassword(accountId, request.oldPass, request.newPass)
            ResponseEntity.ok(mapOf("message" to message))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
}