package d.gajownik.bookit.service

import d.gajownik.bookit.address.AddressService
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.*
import java.util.stream.Collectors



@RestController
class ServicesRestController(
    private val servicesService: ServicesService) {

    @GetMapping("/filter-services")
    fun getServices(model: Model, locale: Locale,
                    @RequestParam(required = false) name: String?,
                    @RequestParam(required = false) industry: List<Long>?,
                    @RequestParam(required = false) address: String?,
                    @RequestParam(required = false) maxDistance: Number?,
                    @RequestParam(required = false) priceMin: BigDecimal?,
                    @RequestParam(required = false) priceMax: BigDecimal?,
                    @RequestParam(required = false) sort: String?): MutableList<List<Any?>>? {

        return servicesService.findAllWithFilters(name, industry, address, maxDistance, priceMin, priceMax, sort, locale)
    }
}