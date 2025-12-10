package net.bscs22.schoolportal.controllers

import net.bscs22.schoolportal.common.ApiResponse
import net.bscs22.schoolportal.dtos.auth.*
import net.bscs22.schoolportal.dtos.block.BlockRequest
import net.bscs22.schoolportal.dtos.college.CollegeRequest
import net.bscs22.schoolportal.dtos.course.CourseRequest
import net.bscs22.schoolportal.dtos.subject.SubjectRequest
import net.bscs22.schoolportal.dtos.subject.SubjectResponse
import net.bscs22.schoolportal.dtos.subject.UpdateSubjectRequest
import net.bscs22.schoolportal.dtos.user.*
import net.bscs22.schoolportal.dtos.enrollment.*
import net.bscs22.schoolportal.entities.academics.*
import net.bscs22.schoolportal.services.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID
import kotlin.math.min

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val registrationService: RegistrationService,
    private val accountService: AccountService,
    private val subjectService: SubjectService,
    private val enrollmentService: EnrollmentService,

    // NEW INJECTIONS (Replaces AcademicManagementService)
    private val collegeService: CollegeService,
    private val courseService: CourseService,
    private val blockService: BlockService
) {

    // ... (Existing User & Auth Endpoints - Search, Register, Management, etc.) ...
    // Note: I'm keeping the controller structure similar but swapping the service calls.

    // --- SEARCH ENDPOINT ---
    @GetMapping("/users/search")
    fun searchUsers(
        @RequestParam("q") query: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<Page<UserResponse>>> {
        val safeSize = min(size, 20)
        val userPage = accountService.searchUsers(query, page, safeSize)
        return ResponseEntity.ok(ApiResponse.success(userPage))
    }

    // ... (Register Endpoints - Student, Professor, Admin) ...
    @PostMapping("/register/student")
    fun registerStudent(@RequestBody req: RegisterStudentRequest): ResponseEntity<ApiResponse<Nothing>> {
        val msg = registrationService.registerStudent(req)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }

    @PostMapping("/register/professor")
    fun registerProfessor(@RequestBody req: RegisterProfessorRequest): ResponseEntity<ApiResponse<Nothing>> {
        val msg = registrationService.registerProfessor(req)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }

    @PostMapping("/register")
    fun registerAdmin(@RequestBody req: RegisterAdminRequest): ResponseEntity<ApiResponse<Nothing>> {
        val msg = registrationService.registerAdmin(req)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }

    // ... (User Management - Update, List, Reset Password) ...
    @PutMapping("/users/{accountId}")
    fun updateUser(
        @PathVariable accountId: UUID,
        @RequestBody req: UpdateUserRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val msg = accountService.updateAccountDetails(accountId, req)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }

    @GetMapping("/users")
    fun listAllUsers(): ResponseEntity<ApiResponse<List<UserResponse>>> {
        val userList = accountService.getAllUserDetails()
        return ResponseEntity.ok(ApiResponse.success(userList))
    }

    @PutMapping("/users/{accountId}/password")
    fun resetUserPassword(
        @PathVariable accountId: UUID,
        @RequestBody req: AdminResetPasswordRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val msg = accountService.adminResetPassword(accountId, req.newPass)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }

    // =========================================================================
    // ACADEMIC MANAGEMENT ENDPOINTS (UPDATED TO USE NEW SERVICES)
    // =========================================================================

    // --- COLLEGES ---
    @GetMapping("/colleges")
    fun getColleges(@RequestParam("q", required = false, defaultValue = "") query: String): ResponseEntity<ApiResponse<List<College>>> {
        return ResponseEntity.ok(ApiResponse.success(collegeService.getAllColleges(query)))
    }

    @PostMapping("/colleges")
    fun createCollege(@RequestBody req: CollegeRequest): ResponseEntity<ApiResponse<College>> {
        return ResponseEntity.ok(ApiResponse.success(collegeService.createCollege(req)))
    }

    @PutMapping("/colleges/{code}")
    fun updateCollege(@PathVariable code: String, @RequestBody req: CollegeRequest): ResponseEntity<ApiResponse<College>> {
        return ResponseEntity.ok(ApiResponse.success(collegeService.updateCollege(code, req)))
    }

    @DeleteMapping("/colleges/{code}")
    fun deleteCollege(@PathVariable code: String): ResponseEntity<ApiResponse<Nothing>> {
        collegeService.deleteCollege(code)
        return ResponseEntity.ok(ApiResponse.success("Deleted"))
    }

    // --- COURSES ---
    @GetMapping("/courses")
    fun getCourses(@RequestParam("q", required = false, defaultValue = "") query: String): ResponseEntity<ApiResponse<List<Course>>> {
        return ResponseEntity.ok(ApiResponse.success(courseService.getAllCourses(query)))
    }

    @PostMapping("/courses")
    fun createCourse(@RequestBody req: CourseRequest): ResponseEntity<ApiResponse<Course>> {
        return ResponseEntity.ok(ApiResponse.success(courseService.createCourse(req)))
    }

    @PutMapping("/courses/{code}")
    fun updateCourse(@PathVariable code: String, @RequestBody req: CourseRequest): ResponseEntity<ApiResponse<Course>> {
        return ResponseEntity.ok(ApiResponse.success(courseService.updateCourse(code, req)))
    }

    @DeleteMapping("/courses/{code}")
    fun deleteCourse(@PathVariable code: String): ResponseEntity<ApiResponse<Nothing>> {
        courseService.deleteCourse(code)
        return ResponseEntity.ok(ApiResponse.success("Deleted"))
    }

    // --- BLOCKS ---
    @GetMapping("/blocks")
    fun getBlocks(@RequestParam("q", required = false, defaultValue = "") query: String): ResponseEntity<ApiResponse<List<Block>>> {
        return ResponseEntity.ok(ApiResponse.success(blockService.getAllBlocks(query)))
    }

    @PostMapping("/blocks")
    fun createBlock(@RequestBody req: BlockRequest): ResponseEntity<ApiResponse<Block>> {
        return ResponseEntity.ok(ApiResponse.success(blockService.createBlock(req)))
    }

    @PutMapping("/blocks/{id}")
    fun updateBlock(@PathVariable id: Long, @RequestBody req: BlockRequest): ResponseEntity<ApiResponse<Block>> {
        return ResponseEntity.ok(ApiResponse.success(blockService.updateBlock(id, req)))
    }

    @DeleteMapping("/blocks/{id}")
    fun deleteBlock(@PathVariable id: Long): ResponseEntity<ApiResponse<Nothing>> {
        blockService.deleteBlock(id)
        return ResponseEntity.ok(ApiResponse.success("Deleted"))
    }

    // --- SUBJECT CRUD ---
    @GetMapping("/subjects")
    fun getAllSubjects(
        @RequestParam("q", required = false, defaultValue = "") query: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<ApiResponse<Page<SubjectResponse>>> {
        val safeSize = min(size, 50)
        val pageable = PageRequest.of(page, safeSize, Sort.by("subjectCode").ascending())
        return ResponseEntity.ok(ApiResponse.success(subjectService.getAllSubjects(query, pageable)))
    }

    @PostMapping("/subjects")
    fun createSubject(@RequestBody req: SubjectRequest): ResponseEntity<ApiResponse<Subject>> {
        val created = subjectService.createSubject(
            req.subjectCode, req.subjectName, req.lecUnits, req.labUnits, req.prerequisiteCode, req.courseCode
        )
        return ResponseEntity.ok(ApiResponse.success(created, "Subject created successfully"))
    }

    @PutMapping("/subjects/{subjectCode}")
    fun updateSubject(
        @PathVariable subjectCode: String,
        @RequestBody req: UpdateSubjectRequest
    ): ResponseEntity<ApiResponse<Subject>> {
        val updated = subjectService.updateSubject(subjectCode, req)
        return ResponseEntity.ok(ApiResponse.success(updated, "Subject updated successfully"))
    }

    @DeleteMapping("/subjects/{subjectCode}")
    fun deleteSubject(@PathVariable subjectCode: String): ResponseEntity<ApiResponse<Nothing>> {
        val msg = subjectService.deleteSubject(subjectCode)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }

    // --- ENROLLMENT MANAGEMENT ---
    @GetMapping("/enrollments/pending")
    fun getPendingEnrollments(): ResponseEntity<ApiResponse<List<AdminEnrollmentDetailResponse>>> {
        val pending = enrollmentService.getPendingEnrollments()
        return ResponseEntity.ok(ApiResponse.success(pending))
    }

    @PostMapping("/approve/{enrollmentId}")
    fun approveEnrollment(@PathVariable enrollmentId: Long): ResponseEntity<ApiResponse<Nothing>> {
        val msg = enrollmentService.approveEnrollment(enrollmentId)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }

    @PostMapping("/reject/{enrollmentId}")
    fun rejectEnrollment(
        @PathVariable enrollmentId: Long,
        @RequestBody req: RejectEnrollmentRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val msg = enrollmentService.rejectEnrollment(enrollmentId, req.reason)
        return ResponseEntity.ok(ApiResponse.success(msg))
    }
}