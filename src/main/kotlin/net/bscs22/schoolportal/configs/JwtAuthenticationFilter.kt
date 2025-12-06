package net.bscs22.schoolportal.configs

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.bscs22.schoolportal.services.JwtService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter (
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    // --- NEW: Skip the filter entirely for login/register endpoints ---
    @Override
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        // Return 'true' if the filter should NOT run for this path
        val path = request.servletPath

        // Explicitly bypasses both login and register
        return path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/register")
    }
    // -----------------------------------------------------------------

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        // 1. Check for token presence and format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return // IMPORTANT: Exit here if the token is null/missing
        }

        val jwt = authHeader.substring(7)
        val userEmail = jwtService.extractUsername(jwt)

        // 2. Validate token and ensure no authentication is already present
        if (userEmail != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = this.userDetailsService.loadUserByUsername(userEmail as String?)

            if (jwtService.isTokenValid(jwt, userDetails)) {
                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                // Set the user in the security context
                SecurityContextHolder.getContext().authentication = authToken
            }
        }

        // 3. Continue the filter chain
        filterChain.doFilter(request, response)
    }
}