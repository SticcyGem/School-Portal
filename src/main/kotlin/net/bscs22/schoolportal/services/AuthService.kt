package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.models.Account
import net.bscs22.schoolportal.models.Professor
import net.bscs22.schoolportal.models.Student
import net.bscs22.schoolportal.models.UserProfile
import net.bscs22.schoolportal.models.enums.AccountStatus
import net.bscs22.schoolportal.models.enums.EducationLevel
import net.bscs22.schoolportal.models.enums.EmployeeType
import net.bscs22.schoolportal.models.enums.StudentType
import net.bscs22.schoolportal.repositories.AccountRepository
import net.bscs22.schoolportal.repositories.ProfessorRepository
import net.bscs22.schoolportal.repositories.StudentRepository
import net.bscs22.schoolportal.repositories.UserProfileRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val accountsRepository: AccountRepository,
    private val userProfileRepository: UserProfileRepository,
    private val studentRepository: StudentRepository,
    private val professorRepository: ProfessorRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) {

    fun authenticate(
        email: String,
        rawPassword: String
    ): Map<String, Any>? {
        val user = accountsRepository.findByEmail(email) ?: return null
        if (user.status != AccountStatus.ACTIVE) {
            return null
        }
        if (passwordEncoder.matches(rawPassword, user.passwordHash)) {
            val primaryRole = user.roles.firstOrNull()?.roleName ?: "UNKNOWN"
            val token = jwtService.generateToken(user.email, user.accountId, primaryRole)
            return mapOf(
                "token" to token,
                "accountId" to user.accountId.toString(),
                "role" to primaryRole
            )
        }
        return null
    }

    @Transactional
    fun registerStudent(
        email: String,
        rawPassword: String,
        firstName: String,
        lastName: String,
        studentNo: Long,
        educationLevel: EducationLevel,
        studentType: StudentType
    ): String {
        if (accountsRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already in use")
        }
        if (studentRepository.existsByStudentNo(studentNo)) {
            throw IllegalArgumentException("Student No already in use")
        }
        val account = accountsRepository.save(Account(email = email, passwordHash = passwordEncoder.encode(rawPassword)))
        userProfileRepository.save(UserProfile(accountId = account.accountId, firstName = firstName, lastName = lastName))
        studentRepository.save(
            Student(
                accountId = account.accountId,
                studentNo = studentNo,
                educationLevel = educationLevel,
                studentType = studentType
            )
        )
        accountsRepository.addRole(account.accountId, 1L)
        return "Student created: $studentNo"
    }

    @Transactional
    fun registerProfessor(
        email: String,
        rawPassword: String,
        firstName: String,
        lastName: String,
        professorId: String,
        employeeType: EmployeeType
    ): String {
        if (accountsRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already in use")
        }
        if (professorRepository.existsByProfessorId(professorId)) {
            throw IllegalArgumentException("Professor ID already in use")
        }
        val account = accountsRepository.save(Account(email = email, passwordHash = passwordEncoder.encode(rawPassword)))
        userProfileRepository.save(UserProfile(accountId = account.accountId, firstName = firstName, lastName = lastName))
        professorRepository.save(
            Professor(
                accountId = account.accountId,
                professorId = professorId,
                employeeType = employeeType
            )
        )
        accountsRepository.addRole(account.accountId, 2L)
        return "Professor created: $professorId"
    }

    @Transactional
    fun registerAdmin(
        email: String,
        rawPassword: String,
        firstName: String,
        lastName: String
    ): String {
        if (accountsRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already in use")
        }
        val newAccount = Account(
            email = email,
            passwordHash = passwordEncoder.encode(rawPassword)
        )
        val savedAccount = accountsRepository.save(newAccount)
        val profile = UserProfile(
            accountId = savedAccount.accountId,
            firstName = firstName,
            lastName = lastName
        )
        userProfileRepository.save(profile)
        savedAccount.accountId.let { id ->
            accountsRepository.addRole(id, 3L)
        }
        return "Admin account created successfully for ${savedAccount.email}"
    }
}