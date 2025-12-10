package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.common.ApiResponse
import net.bscs22.schoolportal.dtos.grading.ComponentRequest
import net.bscs22.schoolportal.dtos.grading.GradeSheetResponse
import net.bscs22.schoolportal.dtos.grading.GradeSubmissionRequest
import net.bscs22.schoolportal.services.GradeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/professor/grading")
class GradeController(
    private val gradeService: GradeService
) {

    @PostMapping("/configure/{sectionId}")
    fun configureScheme(
        @PathVariable sectionId: Long,
        @RequestBody requests: List<ComponentRequest>
    ): ResponseEntity<ApiResponse<Nothing>> {
        val msg = gradeService.configureGradingScheme(sectionId, requests)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }

    @GetMapping("/sheet/{sectionId}")
    fun getSheet(@PathVariable sectionId: Long): ResponseEntity<ApiResponse<GradeSheetResponse>> {
        val data = gradeService.getGradeSheet(sectionId)
        return ResponseEntity.ok(ApiResponse.success(data))
    }

    @PostMapping("/submit")
    fun submitGrades(@RequestBody submissions: List<GradeSubmissionRequest>): ResponseEntity<ApiResponse<Nothing>> {
        val msg = gradeService.submitGrades(submissions)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }
}