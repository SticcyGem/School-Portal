package net.bscs22.schoolportal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
class SchoolPortalApplication

fun main(args: Array<String>) {
    /*println("\n\n====== YOUR GENERATED HASH ======")
    println(passwordHasher("P@ssw0rd"))
    println("=================================\n\n")*/

    runApplication<SchoolPortalApplication>(*args)
}

fun passwordHasher(password: String): String {
    val encoder = BCryptPasswordEncoder(10)
    return encoder.encode(password)
}