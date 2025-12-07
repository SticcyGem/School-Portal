package net.bscs22.schoolportal.mappers

import net.bscs22.schoolportal.dtos.auth.RegisterAdminRequest
import net.bscs22.schoolportal.dtos.auth.RegisterProfessorRequest
import net.bscs22.schoolportal.dtos.auth.RegisterStudentRequest
import net.bscs22.schoolportal.models.Account
import net.bscs22.schoolportal.models.Professor
import net.bscs22.schoolportal.models.Student
import net.bscs22.schoolportal.models.UserProfile
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface AuthMapper {

    // --- STUDENT MAPPINGS ---
    @Mapping(target = "passwordHash", ignore = true)
    fun toAccount(req: RegisterStudentRequest): Account
    fun toUserProfile(req: RegisterStudentRequest): UserProfile
    fun toStudent(req: RegisterStudentRequest): Student

    // --- PROFESSOR MAPPINGS ---
    @Mapping(target = "passwordHash", ignore = true)
    fun toAccount(req: RegisterProfessorRequest): Account
    fun toUserProfile(req: RegisterProfessorRequest): UserProfile
    fun toProfessor(req: RegisterProfessorRequest): Professor

    // --- ADMIN MAPPINGS ---
    @Mapping(target = "passwordHash", ignore = true)
    fun toAccount(req: RegisterAdminRequest): Account
    fun toUserProfile(req: RegisterAdminRequest): UserProfile
}