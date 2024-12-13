package d.gajownik.bookit.industry

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface IndustryRepository : JpaRepository<Industry, Long>{
    @EntityGraph(attributePaths = ["companies"])
    override fun findById(id: Long): Optional<Industry>
}