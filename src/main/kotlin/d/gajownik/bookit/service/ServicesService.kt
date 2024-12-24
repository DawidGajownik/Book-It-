package d.gajownik.bookit.service

import d.gajownik.bookit.address.AddressService
import d.gajownik.bookit.industry.IndustryService
import jakarta.transaction.Transactional
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
@Transactional
class ServicesService(
    private val serviceRepository: ServiceRepository,
    private val addressService: AddressService,
    private val messageSource: MessageSource
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
    fun findAllWithFilters(name: String?, industry: List<Long>?, address: String?, maxDistance: Number?, priceMin: BigDecimal?, priceMax: BigDecimal?, sort: String?, locale: Locale): List<d.gajownik.bookit.service.Service> {
        val all = serviceRepository.findAll()
        val filtered = all.stream()
            .filter { s -> containsStrings(name, s) }
            .filter { s -> matchesIndustries(industry, s) }
            .filter { s -> (priceMax==null || priceMax.toInt()==0) || s.price<=priceMax }
            .filter { s -> priceMin==null || s.price>=priceMin }
            .filter { s -> matchesAddressWithDistance(address, maxDistance, s, locale) }
            .toList()
        return filtered
    }

    fun distance (address: String?, serviceDTO: ServiceDTO, locale: Locale): String? {
        if (address == null) {
            return ""
        }
        return addressService.distanceCalculateFromCoordinates(serviceDTO.latitude, serviceDTO.longitude, address, locale).toString()+"km "+messageSource.getMessage("from.you", null, locale)
    }

    fun matchesAddressWithDistance(address: String?, maxDistance: Number?, service: d.gajownik.bookit.service.Service, locale: Locale): Boolean {
        if (address == null || maxDistance == null) {
            return true
        }
        return maxDistance.toDouble() >= addressService.distanceCalculate(service.company!!.address, address, locale)!!
    }

    fun matchesIndustries(industries: List<Long>?, service: d.gajownik.bookit.service.Service): Boolean {
        if (industries.isNullOrEmpty()) {
            return true
        }
        val serviceIndustries = service.company?.industry
        for (serviceIndustry in serviceIndustries!!) {
            for (industry in industries) {
                if (serviceIndustry.id == industry) {
                    return true
                }
            }
        }
        return false
    }
    fun containsStrings(query: String?, service: d.gajownik.bookit.service.Service): Boolean {
        val splitQuery = query?.lowercase()?.split(" ")
        val serviceName = service.name.lowercase()
        val serviceDescription = service.description.lowercase()
        val companyName = service.company?.name?.lowercase()
        if (splitQuery != null) {
            for (i in splitQuery.indices) {
                if (companyName != null) {
                    if (!serviceName.contains(splitQuery[i]) &&
                        !serviceDescription.contains(splitQuery[i]) &&
                        !companyName.contains(splitQuery[i])
                    ) {
                        return false
                    }
                }
            }
        }
        return true
    }
}