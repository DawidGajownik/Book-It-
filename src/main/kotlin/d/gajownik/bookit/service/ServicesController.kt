package d.gajownik.bookit.service

import d.gajownik.bookit.industry.IndustryService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.util.Locale

@Controller
class ServicesController (
    private val serviceService: ServicesService,
    private val industryService: IndustryService
){
    @GetMapping("services")
    fun offersGet(model: Model, locale: Locale): String {
        model.addAttribute("industries", industryService.findAllAndTranslate(locale))
        model.addAttribute("services", serviceService.findAll())
        return "services"
    }
}