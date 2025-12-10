package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.dtos.auth.AuthResponse
import net.bscs22.schoolportal.dtos.auth.LoginRequest
import net.bscs22.schoolportal.repositories.AccountRepository
import net.bscs22.schoolportal.repositories.ProfessorDetailRepository
import net.bscs22.schoolportal.repositories.StudentDetailRepository
import net.bscs22.schoolportal.repositories.UserProfileRepository
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthenticationService(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val studentDetailRepository: StudentDetailRepository,
    private val professorDetailRepository: ProfessorDetailRepository,
    private val userProfileRepository: UserProfileRepository
) {

    @Transactional(readOnly = true)
    fun authenticate(req: LoginRequest): AuthResponse {
        val account = accountRepository.findByEmail(req.email)
            ?: throw BadCredentialsException("Invalid credentials") // Use specific exception

        if (!passwordEncoder.matches(req.password, account.passwordHash)) {
            throw BadCredentialsException("Invalid credentials")
        }

        // Determine primary role for Token claims (simplified logic)
        val roleName = account.roles.firstOrNull()?.roleName ?: "USER"
        val token = jwtService.generateToken(account.email, account.accountId, roleName)
        val roles = account.roles.map { it.roleName }

        // Fetch View-Specific Profile Data
        val profileData: Any? = when {
            roles.contains("STUDENT") -> studentDetailRepository.findById(account.accountId).orElse(null)
            roles.contains("PROFESSOR") -> professorDetailRepository.findById(account.accountId).orElse(null)
            else -> {
                val p = userProfileRepository.findById(account.accountId).orElse(null)
                if (p != null) mapOf(
                    "name" to "${p.lastName}, ${p.firstName}",
                    "email" to account.email
                ) else null
            }
        }

        return AuthResponse(token, roles, account.accountId, profileData)
    }
}