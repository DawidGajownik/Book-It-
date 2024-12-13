package d.gajownik.bookit.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class ServicesService (
    private val serviceRepository: ServiceRepository
){
    fun findAll(): List<d.gajownik.bookit.service.Service>{
        return serviceRepository.findAll()
    }
}