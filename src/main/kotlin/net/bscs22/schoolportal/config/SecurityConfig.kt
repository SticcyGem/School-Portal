package net.bscs22.schoolportal.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.session.web.http.DefaultCookieSerializer
import org.springframework.session.web.http.CookieSerializer
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }

            // We keep CSRF disabled because handling CSRF tokens in Java Swing is very difficult.
            // To compensate, we will use "SameSite" cookies (see bean below).
            .csrf { it.disable() }

            .sessionManagement {
                // "IF_REQUIRED" creates a session for the Web Browser (Astro),
                // while the Swing client can remain effectively stateless or session-based as needed.
                it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            }

            .authorizeHttpRequests { auth ->
                // Public Access
                auth.requestMatchers("/api/auth/**").permitAll()
                auth.requestMatchers(
                    "/", "/index.html", "/static/**", "/css/**",
                    "/js/**", "/images/**", "/assets/**", "/favicon.ico"
                ).permitAll()

                // Secured Access
                auth.anyRequest().authenticated()
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        // --- SECURITY IMPROVEMENT ---
        // Instead of "*" (Everywhere), we use specific patterns.
        configuration.allowedOriginPatterns = listOf(
            "http://localhost:[*]",             // Matches localhost:4321, localhost:3000, etc.
            "http://emmanuel-laptop.local:[*]", // Matches your hostname
            "http://10.182.*:[*]"               // Matches your Hotspot IP range (10.182.x.x)
        )
        // ----------------------------

        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    /**
     * This Bean hardens the Session Cookie.
     * Since we disabled CSRF, 'SameSite=Lax' ensures that the browser ONLY sends the cookie
     * if the user is actually on your site, preventing Cross-Site attacks.
     */
    @Bean
    fun cookieSerializer(): CookieSerializer {
        val serializer = DefaultCookieSerializer()
        serializer.setSameSite("Lax")
        serializer.setUseSecureCookie(false) // Set to true only if you enable HTTPS/SSL later
        return serializer
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(10)
    }
}