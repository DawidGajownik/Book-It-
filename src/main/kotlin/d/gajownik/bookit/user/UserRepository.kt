package d.gajownik.bookit.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun findAllByCompanyId(companyId: Long): List<User>
}
