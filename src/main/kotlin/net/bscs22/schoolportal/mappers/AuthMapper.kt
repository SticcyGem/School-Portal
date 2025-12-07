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
import java.util.UUID
import java.util.HashSet

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    imports = [UUID::class, HashSet::class]
)
interface AuthMapper {

    // --- STUDENT ---
    @Mapping(target = "passwordHash", constant = "")
    @Mapping(target = "accountId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "authProvider", constant = "LOCAL")
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "roles", expression = "java(new HashSet<>())")
    fun toAccount(req: RegisterStudentRequest): Account
    fun toUserProfile(req: RegisterStudentRequest): UserProfile
    fun toStudent(req: RegisterStudentRequest): Student

    // --- PROFESSOR ---
    @Mapping(target = "passwordHash", constant = "")
    @Mapping(target = "accountId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "authProvider", constant = "LOCAL")
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "roles", expression = "java(new HashSet<>())")
    fun toAccount(req: RegisterProfessorRequest): Account
    fun toUserProfile(req: RegisterProfessorRequest): UserProfile
    fun toProfessor(req: RegisterProfessorRequest): Professor

    // --- ADMIN ---
    @Mapping(target = "passwordHash", constant = "")
    @Mapping(target = "accountId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "authProvider", constant = "LOCAL")
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "roles", expression = "java(new HashSet<>())")
    fun toAccount(req: RegisterAdminRequest): Account
    fun toUserProfile(req: RegisterAdminRequest): UserProfile
}