package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.services.EnrollmentService
import net.bscs22.schoolportal.services.JwtService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/enrollment")
class EnrollmentController(
    private val enrollmentService: EnrollmentService,
    private val jwtService: JwtService
) {
    data class SubmitEnrollmentRequest(
        val sectionIds: List<Long>
    )

    data class EnrollmentOfferingResponse(
        val termName: String,
        val studentType: String,
        val isBlockBased: Boolean,
        val sections: List<SectionDTO>,
        val enrollmentStatus: String,
        val remarks: String?
    )

    data class SectionDTO(
        val sectionNo: Long,
        val sectionName: String,
        val subjectCode: String,
        val subjectTitle: String,
        val units: Long,
        val schedule: String,
        val status: String
    )

    @GetMapping("/options")
    fun getOptions(@RequestHeader("Authorization") tokenHeader: String): ResponseEntity<Any> {
        return try {
            val accountId = extractAccountId(tokenHeader)
            val response = enrollmentService.getEnrollmentOptions(accountId)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/submit")
    fun submitEnrollment(
        @RequestHeader("Authorization") tokenHeader: String,
        @RequestBody request: SubmitEnrollmentRequest
    ): ResponseEntity<Any> {
        return try {
            val accountId = extractAccountId(tokenHeader)
            val msg = enrollmentService.submitEnrollment(accountId, request.sectionIds)
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    private fun extractAccountId(header: String): UUID {
        val token = header.substring(7)
        val idStr = jwtService.extractClaim(token) { it["accountId"] as String }
        return UUID.fromString(idStr)
    }
}