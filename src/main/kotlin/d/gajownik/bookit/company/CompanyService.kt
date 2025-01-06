package d.gajownik.bookit.company

import d.gajownik.bookit.industry.Industry
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Optional

@Service
@Transactional
class CompanyService(
    private val companyRepository: CompanyRepository
) {
    fun findByUserId(userId: Long): Company? {
        return companyRepository.findByUsersId(userId)
    }
    fun save(company: Company) {
        companyRepository.save(company)
    }
    fun findById(id: Long): Optional<Company> {
        return companyRepository.findById(id)
    }
}