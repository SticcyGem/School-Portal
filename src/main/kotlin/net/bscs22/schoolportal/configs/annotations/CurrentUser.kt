package net.bscs22.schoolportal.configs.annotations

import org.springframework.security.core.annotation.AuthenticationPrincipal
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@AuthenticationPrincipal
annotation class CurrentUser