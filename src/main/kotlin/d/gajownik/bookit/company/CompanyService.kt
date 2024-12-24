package d.gajownik.bookit.company

import d.gajownik.bookit.industry.Industry
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class CompanyService(
    private val companyRepository: CompanyRepository
) {
    fun findAll(): List<Company>{
        return companyRepository.findAll()
    }
    fun findByUserId(userId: Long): Company {
        return companyRepository.findByUsersId(userId)
    }
}