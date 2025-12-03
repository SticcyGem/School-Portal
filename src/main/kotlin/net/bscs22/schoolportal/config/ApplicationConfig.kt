package net.bscs22.schoolportal.config

import net.bscs22.schoolportal.repository.LoginRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class ApplicationConfig(
    private val loginRepository: LoginRepository,
) {

    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { email ->
            val user = loginRepository.findByEmail(email)
                ?: throw UsernameNotFoundException("User not found")

            org.springframework.security.core.userdetails.User
                .withUsername(user.email)
                .password(user.passwordHash)
                .roles(when (user.roleNo) {
                    1L -> "STUDENT"
                    2L -> "PROFESSOR"
                    3L -> "ADMIN"
                    else -> "USER"
                })
                .build()
        }
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider(userDetailsService())
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(10)
    }
}