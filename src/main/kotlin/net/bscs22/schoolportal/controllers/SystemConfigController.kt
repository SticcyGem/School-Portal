package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.common.ApiResponse
import net.bscs22.schoolportal.dtos.enrollment.SystemStatusResponse
import net.bscs22.schoolportal.dtos.enrollment.UpdateTermConfigRequest
import net.bscs22.schoolportal.services.AdminSystemService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/system")
class SystemConfigController(
    private val systemService: AdminSystemService
) {

    @GetMapping("/status")
    fun getCurrentStatus(): ResponseEntity<ApiResponse<SystemStatusResponse>> {
        val status = systemService.getCurrentSystemStatus()
        return ResponseEntity.ok(ApiResponse.success(status))
    }

    @PostMapping("/config")
    fun updateConfig(@RequestBody req: UpdateTermConfigRequest): ResponseEntity<ApiResponse<Nothing>> {
        systemService.updateEnrollmentConfig(req)
        return ResponseEntity.ok(ApiResponse.success("System configuration updated."))
    }
}