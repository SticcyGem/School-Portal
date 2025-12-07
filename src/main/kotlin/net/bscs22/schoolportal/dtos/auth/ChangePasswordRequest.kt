package net.bscs22.schoolportal.dtos.auth

data class ChangePasswordRequest(
    val oldPass: String,
    val newPass: String
)
