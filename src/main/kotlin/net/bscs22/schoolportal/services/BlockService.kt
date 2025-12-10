package net.bscs22.schoolportal.services

import net.bscs22.schoolportal.dtos.block.BlockRequest
import net.bscs22.schoolportal.entities.academics.Block
import net.bscs22.schoolportal.repositories.BlockRepository
import net.bscs22.schoolportal.repositories.CourseRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BlockService(
    private val blockRepo: BlockRepository,
    private val courseRepo: CourseRepository
) {
    fun getAllBlocks(query: String): List<Block> {
        val sort = Sort.by("course.courseCode", "yearLevel", "blockNumber")
        return if (query.isBlank()) blockRepo.findAll(sort)
        else blockRepo.searchBlocks(query, sort)
    }

    @Transactional
    fun createBlock(req: BlockRequest): Block {
        val course = courseRepo.findById(req.courseCode)
            .orElseThrow { IllegalArgumentException("Course '${req.courseCode}' not found") }

        val block = Block(
            blockNo = null, // Auto-generated
            yearLevel = req.yearLevel,
            blockNumber = req.blockNumber,
            course = course
        )
        return blockRepo.save(block)
    }

    @Transactional
    fun updateBlock(id: Long, req: BlockRequest): Block {
        val block = blockRepo.findById(id)
            .orElseThrow { IllegalArgumentException("Block #$id not found") }

        val course = courseRepo.findById(req.courseCode)
            .orElseThrow { IllegalArgumentException("Course '${req.courseCode}' not found") }

        block.course = course
        block.yearLevel = req.yearLevel
        block.blockNumber = req.blockNumber

        return blockRepo.save(block)
    }

    @Transactional
    fun deleteBlock(id: Long) {
        if (!blockRepo.existsById(id)) throw IllegalArgumentException("Block not found")
        blockRepo.deleteById(id)
    }
}