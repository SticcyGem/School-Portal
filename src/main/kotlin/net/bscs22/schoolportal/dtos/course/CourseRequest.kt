package net.bscs22.schoolportal.dtos.course

import com.fasterxml.jackson.annotation.JsonProperty
import net.bscs22.schoolportal.entities.commons.enums.EducationLevel

data class CourseRequest(
    @field:JsonProperty("course_code") val courseCode: String,
    @field:JsonProperty("course_name") val courseName: String,
    @field:JsonProperty("course_tier") val courseTier: EducationLevel,
    @field:JsonProperty("college_code") val collegeCode: String
)