package d.gajownik.bookit.service

import d.gajownik.bookit.address.AddressController
import d.gajownik.bookit.address.AddressService
import d.gajownik.bookit.industry.IndustryService
import d.gajownik.bookit.user.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

@Controller
class ServicesController(
    private val serviceService: ServicesService,
    private val industryService: IndustryService,
    private val userService: UserService,
    private val addressService: AddressService,
    private val addressController: AddressController
){
    @GetMapping("services")
    fun services(model: Model, locale: Locale,
                  @RequestParam (required = false) name: String?,
                  @RequestParam (required = false) companyName: String?,
                  @RequestParam (required = false) industry: List<Long>?,
                  @RequestParam (required = false) address: String?,
                  @RequestParam (required = false) maxDistance: Number?,
                  @RequestParam (required = false) priceMin: Number?,
                  @RequestParam (required = false) priceMax: Number?,
                  @RequestParam (required = false) sort: String?): String {
        model.addAttribute("processedAddress", address?.let { addressController.processAddress(it, locale) })
        model.addAttribute("industries", industryService.findAllAndTranslate(locale))
        return "services"
    }
    @GetMapping("services-list")
    fun servicesList(model: Model, locale: Locale,
                  @RequestParam (required = false) name: String?,
                  @RequestParam (required = false) companyName: String?,
                  @RequestParam (required = false) industry: List<Long>?,
                  @RequestParam (required = false) address: String?,
                  @RequestParam (required = false) maxDistance: Number?,
                  @RequestParam (required = false) priceMin: Number?,
                  @RequestParam (required = false) priceMax: Number?,
                  @RequestParam (required = false) sort: String?): String {
        model.addAttribute("processedAddress", address?.let { addressController.processAddress(it, locale) })
        model.addAttribute("industries", industryService.findAllAndTranslate(locale))
        return "services-list"
    }
    @GetMapping("services/add/{companyId}")
    fun addService(model: Model, locale: Locale, @PathVariable companyId:Long): String {
        val service: Service? = null
        model.addAttribute("service", service)
        model.addAttribute("users", userService.findAllByCompanyId(companyId))
        return "service-add"
    }
    @PostMapping("services/add")
    fun addServicePost(@ModelAttribute service: Service): String {

        serviceService.save(service)
        return "redirect:/"
    }
    @GetMapping("service/{serviceId}")
    fun serviceDetails(@PathVariable serviceId: Long, model: Model): String {
        model.addAttribute("service", serviceService.findById(serviceId).get())
        return "service"
    }
}