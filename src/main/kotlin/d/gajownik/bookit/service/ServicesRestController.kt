package d.gajownik.bookit.service

import com.sun.jdi.IntegerValue
import jakarta.servlet.http.HttpServletRequest
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.*


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
                    @RequestParam(required = false) sort: String?,
                    @RequestParam(required = false) page: Int, request: HttpServletRequest): MutableList<List<Any?>>? {

        return servicesService.findAllWithFilters(name, industry, address, maxDistance, priceMin, priceMax, sort, page, locale, request)
    }

    @GetMapping("/pages")
    fun getPages(request: HttpServletRequest): Int {
        val pages = request.session.getAttribute("pages-quantity").toString().toDouble().toInt()
        if(pages==null) return 0
        return pages
    }
}