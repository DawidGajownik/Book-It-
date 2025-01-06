package d.gajownik.bookit.service

import d.gajownik.bookit.industry.IndustryService
import d.gajownik.bookit.user.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.util.*

@Controller
class ServicesController(
    private val serviceService: ServicesService,
    private val industryService: IndustryService,
    private val userService: UserService
){

    @GetMapping("services")
    fun services(model: Model, locale: Locale): String {
        model.addAttribute("industries", industryService.findAllAndTranslate(locale))
        return "services"
    }

    @GetMapping("services-list")
    fun servicesList(model: Model, locale: Locale): String {
        model.addAttribute("industries", industryService.findAllAndTranslate(locale))
        return "services-list"
    }

    @GetMapping("services/add/{companyId}")
    fun addService(model: Model, locale: Locale, @PathVariable companyId:Long): String {
        val service: Service? = null
        model.addAttribute("industries", industryService.findAllAndTranslate(locale))
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