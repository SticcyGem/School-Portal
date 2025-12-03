package net.bscs22.schoolportal.service

import net.bscs22.schoolportal.repository.LoginRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val loginRepository: LoginRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) {

    fun authenticate(email: String, rawPassword: String): Map<String, Any>? {
        val user = loginRepository.findByEmail(email) ?: return null

        if (user.status != "Active") {
            return null
        }

        if (passwordEncoder.matches(rawPassword, user.passwordHash)) {
            val roleName = when(user.roleNo) {
                1L -> "STUDENT"
                2L -> "PROFESSOR"
                3L -> "ADMIN"
                else -> "UNKNOWN"
            }
            val token = jwtService.generateToken(user.email, user.accountId, roleName)
            return mapOf(
                "token" to token,
                "accountId" to user.accountId.toString(),
                "role" to roleName
            )
        }

        return null
    }
}