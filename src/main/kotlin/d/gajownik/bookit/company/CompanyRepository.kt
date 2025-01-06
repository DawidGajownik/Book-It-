package d.gajownik.bookit.company

import org.springframework.data.jpa.repository.JpaRepository

interface CompanyRepository : JpaRepository<Company, Long>{
    fun findByUsersId (userid: Long): Company?
}