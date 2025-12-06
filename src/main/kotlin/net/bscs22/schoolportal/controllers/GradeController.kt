package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.services.GradeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/professor/grading")
class GradeController(
    private val gradeService: GradeService
) {

    // ==========================================
    // DTOs (Data Access Objects / Data Transfer Objects)
    // ==========================================

    // --- INPUTS (Requests) ---
    data class ComponentRequest(
        val name: String,
        val weightPercent: Long,
        val children: List<ChildComponentRequest>
    )

    data class ChildComponentRequest(
        val name: String,
        val maxScore: Long
    )

    data class GradeSubmissionRequest(
        val enrollmentId: Long,
        val componentId: Long,
        val score: Long
    )

    // --- OUTPUTS (Responses) ---
    data class GradeSheetResponse(
        val components: List<HeaderDTO>,
        val students: List<StudentRowDTO>
    )

    data class HeaderDTO(
        val id: Long,
        val name: String,
        val maxScore: Long,
        val parentName: String?
    )

    data class StudentRowDTO(
        val enrollmentId: Long,
        val studentName: String,
        val studentId: String,
        val grades: Map<Long, Long> // ComponentID -> Score
    )
    // ==========================================


    @PostMapping("/configure/{sectionId}")
    fun configureScheme(
        @PathVariable sectionId: Long,
        @RequestBody requests: List<ComponentRequest>
    ): ResponseEntity<Any> {
        return try {
            val msg = gradeService.configureGradingScheme(sectionId, requests)
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @GetMapping("/sheet/{sectionId}")
    fun getSheet(@PathVariable sectionId: Long): ResponseEntity<Any> {
        return try {
            val data = gradeService.getGradeSheet(sectionId)
            ResponseEntity.ok(data)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/submit")
    fun submitGrades(@RequestBody submissions: List<GradeSubmissionRequest>): ResponseEntity<Any> {
        return try {
            val msg = gradeService.submitGrades(submissions)
            ResponseEntity.ok(mapOf("message" to msg))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
}