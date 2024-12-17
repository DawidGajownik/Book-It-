package d.gajownik.bookit.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class ServicesService (
    private val serviceRepository: ServiceRepository
){
    fun findAll(): List<d.gajownik.bookit.service.Service>{
        return serviceRepository.findAll()
    }
    fun findAllByCompanyId(companyId: Long): List<d.gajownik.bookit.service.Service>{
        return serviceRepository.findAllByCompanyId(companyId)
    }
    fun save(d : d.gajownik.bookit.service.Service) {
        serviceRepository.save(d)
    }
    fun findById(id: Long): Optional<d.gajownik.bookit.service.Service> {
        return serviceRepository.findById(id)
    }
}