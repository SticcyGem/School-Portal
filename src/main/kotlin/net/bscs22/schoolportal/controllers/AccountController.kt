package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.common.ApiResponse
import net.bscs22.schoolportal.configs.annotations.CurrentUser
import net.bscs22.schoolportal.dtos.auth.ChangePasswordRequest
import net.bscs22.schoolportal.services.AccountService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/account")
class AccountController(
    private val accountService: AccountService
) {

    @PostMapping("/change-password")
    fun changePassword(
        @CurrentUser accountId: UUID,
        @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val message = accountService.changePassword(accountId, request.oldPass, request.newPass)
        return ResponseEntity.ok(ApiResponse.success(message))
    }
}