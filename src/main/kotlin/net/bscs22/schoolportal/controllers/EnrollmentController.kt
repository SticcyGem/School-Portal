package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.dtos.enrollment.EnrollmentOfferingResponse
import net.bscs22.schoolportal.dtos.enrollment.SubmitEnrollmentRequest
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

    @GetMapping("/options")
    fun getOptions(@RequestHeader("Authorization") tokenHeader: String): ResponseEntity<Any> {
        return try {
            val accountId = extractAccountId(tokenHeader)
            val response: EnrollmentOfferingResponse = enrollmentService.getEnrollmentOptions(accountId)
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
            val msg = enrollmentService.submitEnrollment(accountId, request.sectionId)
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    private fun extractAccountId(header: String): UUID {
        if (!header.startsWith("Bearer ")) throw IllegalArgumentException("Invalid Token Format")
        val token = header.substring(7)
        val idStr = jwtService.extractClaim(token) { it["accountId"] as String }
        return UUID.fromString(idStr)
    }
}