package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.controllers.GradeController
import net.bscs22.schoolportal.models.*
import net.bscs22.schoolportal.repositories.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GradeService(
    private val gradeComponentRepository: GradeComponentRepository,
    private val sectionRepository: SectionRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val gradeRepository: GradeRepository,
    private val sectionGradeRowRepository: SectionGradeRowRepository
) {

    // =========================================================================
    // FEATURE 1: CONFIGURATION
    // =========================================================================

    @Transactional
    fun configureGradingScheme(sectionId: Long, requests: List<GradeController.ComponentRequest>): String {
        val section = sectionRepository.findById(sectionId)
            .orElseThrow { IllegalArgumentException("Section not found") }

        // 1. Validation
        val totalWeight = requests.sumOf { it.weightPercent }
        if (totalWeight != 100L) {
            throw IllegalArgumentException("Total weight must be exactly 100%. Current: $totalWeight%")
        }

        // 2. Clear existing
        val existing = gradeComponentRepository.findBySection_SectionNoOrderByGcNoAsc(sectionId)
        gradeComponentRepository.deleteAll(existing)
        // CRITICAL FIX: Flush changes immediately so the DB trigger sees the table as empty
        // before we start inserting new rows. Without this, the trigger sees (Old + New) > 100%.
        gradeComponentRepository.flush()

        // 3. Build new Hierarchy
        for (parentReq in requests) {
            // Calculate Parent Max Score by summing children
            val parentMax = parentReq.children.sumOf { it.maxScore }

            // Safety Check: Parent cannot have 0 max score if it has children
            if (parentMax <= 0 && parentReq.children.isNotEmpty()) {
                throw IllegalArgumentException("Parent component ${parentReq.name} must have a total score > 0")
            }

            val parent = GradeComponent(
                gcName = parentReq.name,
                percent = parentReq.weightPercent,
                maxScore = parentMax,
                section = section,
                parentGc = null
            )
            val savedParent = gradeComponentRepository.save(parent)

            for (childReq in parentReq.children) {
                // FIX: Calculate Relative Percentage (Child Score / Parent Total * 100)
                // This satisfies the DB constraint 'percent > 0'
                val relativePercent = if (parentMax > 0) {
                    ((childReq.maxScore.toDouble() / parentMax.toDouble()) * 100).toLong()
                } else {
                    0
                }

                val child = GradeComponent(
                    gcName = childReq.name,
                    percent = relativePercent, // Now passes (e.g., 50%) instead of 0
                    maxScore = childReq.maxScore,
                    section = section,
                    parentGc = savedParent
                )
                gradeComponentRepository.save(child)
            }
        }

        return "Grading scheme configured successfully."
    }

    // =========================================================================
    // FEATURE 2: GRADING SHEET (View Optimized)
    // =========================================================================

    @Transactional(readOnly = true)
    fun getGradeSheet(sectionId: Long): GradeController.GradeSheetResponse {
        // 1. Headers
        val allComponents = gradeComponentRepository.findBySection_SectionNoOrderByGcNoAsc(sectionId)
        val leafComponents = allComponents.filter { it.parentGc != null }

        val headers = leafComponents.map {
            GradeController.HeaderDTO(it.gcNo!!, it.gcName, it.maxScore, it.parentGc?.gcName)
        }

        // 2. Data (View)
        val rawRows = sectionGradeRowRepository.findBySectionNo(sectionId)

        // 3. Grouping
        val students = rawRows.groupBy { it.enrollmentNo }.map { (enrollmentId, rows) ->
            val studentInfo = rows.first()
            val displayId = "STUDENT-${studentInfo.studentAccountId.toString().take(4)}"

            val gradeMap = rows
                .filter { it.componentId != null && it.rawScore != null }
                .associate { it.componentId!! to it.rawScore!! }

            GradeController.StudentRowDTO(
                enrollmentId = enrollmentId,
                studentName = studentInfo.studentName,
                studentId = displayId,
                grades = gradeMap
            )
        }

        return GradeController.GradeSheetResponse(headers, students)
    }

    // =========================================================================
    // FEATURE 3: SUBMIT GRADES (Batch Optimized)
    // =========================================================================

    @Transactional
    fun submitGrades(submissions: List<GradeController.GradeSubmissionRequest>): String {
        if (submissions.isEmpty()) return "No grades to save."

        // 1. Batch Fetch Dependencies (3 Queries total)
        val enrollmentIds = submissions.map { it.enrollmentId }.distinct()
        val componentIds = submissions.map { it.componentId }.distinct()

        val enrollments = enrollmentRepository.findAllById(enrollmentIds).associateBy { it.enrollmentNo }
        val components = gradeComponentRepository.findAllById(componentIds).associateBy { it.gcNo }

        // Fetch all existing grades for these students to check for updates
        val existingGrades = gradeRepository.findByEnrollment_EnrollmentNoIn(enrollmentIds)

        // 2. Process in Memory
        for (sub in submissions) {
            val enrollment = enrollments[sub.enrollmentId]
                ?: throw IllegalArgumentException("Enrollment ${sub.enrollmentId} not found")
            val component = components[sub.componentId]
                ?: throw IllegalArgumentException("Component ${sub.componentId} not found")

            // Validate
            if (sub.score < 0 || sub.score > component.maxScore) {
                throw IllegalArgumentException("Score ${sub.score} is invalid for ${component.gcName}")
            }

            // Find match in the pre-fetched list
            val match = existingGrades.find {
                it.enrollment.enrollmentNo == sub.enrollmentId &&
                        it.gradeComponent.gcNo == sub.componentId
            }

            if (match != null) {
                match.rawScore = sub.score
                gradeRepository.save(match)
            } else {
                val newGrade = Grade(
                    gradeComponent = component,
                    enrollment = enrollment,
                    rawScore = sub.score
                )
                gradeRepository.save(newGrade)
            }
        }

        return "Grades saved successfully."
    }
}