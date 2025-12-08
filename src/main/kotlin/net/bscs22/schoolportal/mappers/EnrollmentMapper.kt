package net.bscs22.schoolportal.mappers

import net.bscs22.schoolportal.dtos.enrollment.SectionOfferingResponse
import net.bscs22.schoolportal.dtos.enrollment.SectionApprovalResponse
import net.bscs22.schoolportal.entities.academics.Section
import net.bscs22.schoolportal.entities.commons.enums.SectionStatus
import net.bscs22.schoolportal.entities.views.PendingEnrollmentDetail
import org.mapstruct.*

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
abstract class EnrollmentMapper {
    // ADMIN VIEW MAPPING
    @Mapping(target = "subjectTitle", source = "subjectName")
    @Mapping(target = "schedule", source = "fullSchedule")
    abstract fun toSectionApprovalResponse(view: PendingEnrollmentDetail): SectionApprovalResponse

    // STUDENT OFFERING MAPPING
    @Mapping(target = "sectionName", source = "section", qualifiedByName = ["formatSectionName"])
    @Mapping(target = "subjectCode", source = "subject.subjectCode")
    @Mapping(target = "subjectTitle", source = "subject.subjectName")
    @Mapping(target = "units", source = "section", qualifiedByName = ["calculateTotalUnits"])
    @Mapping(target = "schedule", source = "section", qualifiedByName = ["formatSchedule"])
    @Mapping(target = "status", source = "section", qualifiedByName = ["determineStatus"])
    abstract fun toSectionOfferingResponse(section: Section): SectionOfferingResponse

    // HELPER FUNCTIONS
    @Named("formatSchedule")
    fun formatSchedule(section: Section): String {
        if (section.schedules.isEmpty()) return "TBA"
        return section.schedules.joinToString(", ") {
            "${it.dayName} ${it.startTime}-${it.endTime} (${it.room.roomName})"
        }
    }

    @Named("formatSectionName")
    fun formatSectionName(section: Section): String {
        val block = section.blocks.firstOrNull()?.block ?: return "Open Section"
        val course = block.course ?: return "Unknown Course"

        return "${course.courseCode} ${block.yearLevel}-${block.blockNumber}"
    }

    @Named("calculateTotalUnits")
    fun calculateTotalUnits(section: Section): Long {
        return section.subject.lecUnits + section.subject.labUnits
    }

    @Named("determineStatus")
    fun determineStatus(section: Section): SectionStatus {
        return if (section.availableSlots > 0) SectionStatus.OPEN else SectionStatus.FULL
    }
}