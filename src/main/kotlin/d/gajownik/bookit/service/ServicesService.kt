package d.gajownik.bookit.service

import d.gajownik.bookit.address.AddressService
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
)
{

    fun findAllByCompanyId(companyId: Long): List<d.gajownik.bookit.service.Service>{
        return serviceRepository.findAllByCompanyId(companyId)
    }

    fun save(d : d.gajownik.bookit.service.Service) {
        serviceRepository.save(d)
    }

    fun findById(id: Long): Optional<d.gajownik.bookit.service.Service> {
        return serviceRepository.findById(id)
    }

    fun findAllWithFilters(name: String?, industry: List<Long>?, address: String?, maxDistance: Number?, priceMin: BigDecimal?, priceMax: BigDecimal?, sort: String?, locale: Locale): MutableList<List<Any?>>? {
        val addressObject = addressService.findDataFromString(address, locale)
        var latitude: Double? = null
        var longitude: Double? = null
        if (addressObject != null) {
            latitude = addressObject.latitude
            longitude = addressObject.longitude
        }
        return serviceRepository.findAll().stream()
            .filter { s -> containsStrings(name, s) }
            .filter { s -> matchesIndustries(industry, s) }
            .filter { s -> (priceMax==null || priceMax.toInt()==0) || s.price<=priceMax }
            .filter { s -> priceMin==null || s.price>=priceMin }
            .map { service: d.gajownik.bookit.service.Service -> ServiceDTO(service) }
            .filter { s -> matchesAddressWithDistance(s.latitude, s.longitude, latitude, longitude, maxDistance, locale) }
            .map { s -> listOf(s, distanceMessage(latitude, longitude, s, locale))}
            .toList()
    }

    fun distanceMessage (latitude: Double?, longitude: Double?, serviceDTO: ServiceDTO, locale: Locale): String? {
        if (latitude == null || longitude == null) {
            return ""
        }
        return addressService.haversine(serviceDTO.latitude, serviceDTO.longitude, latitude, longitude).toString()+"km "+messageSource.getMessage("from.you", null, locale)
    }

    fun matchesAddressWithDistance(serviceDtoLatitude: Double?, serviceDtoLongitude: Double?, addressLatitude: Double?, addressLongitude: Double?, maxDistance: Number?, locale: Locale): Boolean {
        if (serviceDtoLatitude == null || serviceDtoLongitude == null || addressLatitude == null || addressLongitude == null || maxDistance == null || maxDistance == 0) {
            return true
        }
        val result = addressService.haversine(serviceDtoLatitude, serviceDtoLongitude, addressLatitude, addressLongitude) ?: return true
        return maxDistance.toDouble() >= result
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