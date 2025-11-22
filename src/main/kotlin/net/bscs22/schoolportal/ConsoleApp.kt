package net.bscs22.schoolportal

import net.bscs22.schoolportal.model.SchoolProfile
import net.bscs22.schoolportal.model.StudentProfile
import net.bscs22.schoolportal.model.ProfessorProfile
import net.bscs22.schoolportal.repository.AccountRepository
import net.bscs22.schoolportal.service.ProfileService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.Scanner

@Component
class ConsoleApp(
    private val accountRepo: AccountRepository,
    private val studentService: ProfileService<StudentProfile>,
    private val professorService: ProfileService<ProfessorProfile>
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val scanner = Scanner(System.`in`)

        println("==========================================")
        println("   OOP DEMONSTRATION: SCHOOL PORTAL      ")
        println("==========================================")

        while (true) {
            print("\nEnter Email (or 'exit'): ")
            val email = scanner.next()
            if (email.equals("exit", ignoreCase = true)) break

            print("Enter Password: ")
            val password = scanner.next()

            val account = accountRepo.findByEmail(email)
            if (account == null) {
                println("Error: Email not found.")
                continue
            }

            val storedPass = account.passwordHash.replace("{noop}", "")
            if (password != storedPass) {
                println("Error: Incorrect password.")
                continue
            }

            println("Authentication Successful! Fetching Profile Data...")

            // Polymorphic profile loading
            val profile: SchoolProfile? = studentService.load(account.accountId)
                ?: professorService.load(account.accountId)

            if (profile == null) {
                println("Login successful, but no profile found for this account.")
                continue
            }

            // Display based on type
            when (profile) {
                is StudentProfile -> displayStudent(profile)
                is ProfessorProfile -> displayProfessor(profile)
                else -> println("Unknown profile type")
            }
        }

        println("Exiting application. Goodbye!")
    }

    private fun displayStudent(student: StudentProfile) {
        println("\n------------------------------------------")
        println(" STUDENT PROFILE")
        println("------------------------------------------")
        println(" Student Name : ${student.studentName}")
        println(" Student No   : ${student.studentNo}")
        println(" Account ID   : ${student.accountId}")
        println(" Type         : ${student.studentType}")
        println(" Year Level   : ${student.yearLevel}")
        println(" Block        : ${student.blockName}")
        println(" Course       : ${student.courseCode}")
        println("------------------------------------------")
    }

    private fun displayProfessor(professor: ProfessorProfile) {
        println("\n------------------------------------------")
        println(" PROFESSOR PROFILE")
        println("------------------------------------------")
        println(" Professor Name : ${professor.professorName}")
        println(" Professor No   : ${professor.professorNo}")
        println(" Account ID     : ${professor.accountId}")
        println(" Employee Type  : ${professor.employeeType}")
        println("------------------------------------------")
    }
}
