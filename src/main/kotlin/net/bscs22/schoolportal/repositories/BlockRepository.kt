package net.bscs22.schoolportal.repositories

import net.bscs22.schoolportal.models.Block
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlockRepository : JpaRepository<Block, Long>