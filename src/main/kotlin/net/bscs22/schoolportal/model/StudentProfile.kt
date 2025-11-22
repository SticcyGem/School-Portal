package net.bscs22.schoolportal.model

/**
 * Represents the detailed information of a student.
 *
 * @property accountId     The account identifier associated with the student.
 * @property email         The official email address of the student.
 * @property studentNo     The unique numeric student identifier.
 * @property studentName   The full name of the student.
 * @property studentType   The classification of the student (e.g., regular, irregular).
 * @property yearLevel     The student's current year level (e.g., 1 to 4).
 * @property blockNumber   The numerical block assignment.
 * @property blockName     The name of the block the student belongs to.
 * @property courseCode    The code of the student's enrolled course.
 */
data class StudentProfile(
    override val accountId: String,
    override val email: String,
    val studentNo: Int,
    val studentName: String,
    val studentType: String,
    val yearLevel: Int,
    val blockNumber: Int,
    val blockName: String,
    val courseCode: String
) : SchoolProfile