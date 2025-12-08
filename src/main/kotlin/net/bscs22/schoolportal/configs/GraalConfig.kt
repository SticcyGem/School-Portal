package net.bscs22.schoolportal.configs

import net.bscs22.schoolportal.common.ApiResponse
import net.bscs22.schoolportal.dtos.auth.AuthResponse
import net.bscs22.schoolportal.dtos.auth.LoginRequest
import net.bscs22.schoolportal.dtos.enrollment.EnrollmentResponse
import net.bscs22.schoolportal.dtos.enrollment.EnrollmentOfferingResponse
import net.bscs22.schoolportal.dtos.user.UserResponse
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.context.annotation.Configuration

@Configuration
@RegisterReflectionForBinding(
    classes = [
        ApiResponse::class,
        AuthResponse::class,
        UserResponse::class,
        EnrollmentResponse::class,
        EnrollmentOfferingResponse::class,
        LoginRequest::class
    ]
)
class GraalConfig