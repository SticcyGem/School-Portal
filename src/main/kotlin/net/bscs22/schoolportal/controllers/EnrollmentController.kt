package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.common.ApiResponse
import net.bscs22.schoolportal.configs.annotations.CurrentUser
import net.bscs22.schoolportal.dtos.enrollment.EnrollmentOfferingResponse
import net.bscs22.schoolportal.dtos.enrollment.SubmitEnrollmentRequest
import net.bscs22.schoolportal.services.EnrollmentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/enrollment")
class EnrollmentController(
    private val enrollmentService: EnrollmentService
) {

    @GetMapping("/options")
    fun getOptions(@CurrentUser accountId: UUID): ResponseEntity<ApiResponse<EnrollmentOfferingResponse>> {
        val response = enrollmentService.getEnrollmentOptions(accountId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping("/submit")
    fun submitEnrollment(
        @CurrentUser accountId: UUID,
        @RequestBody request: SubmitEnrollmentRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val msg = enrollmentService.submitEnrollment(accountId, request.sectionId)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }
}