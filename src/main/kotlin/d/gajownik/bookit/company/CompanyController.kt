package d.gajownik.bookit.company

import d.gajownik.bookit.service.ServicesService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class CompanyController (
    private val servicesService: ServicesService
){
    @GetMapping("/company/{companyId}")
    fun company(model: Model, @PathVariable companyId: Long): String {
        model.addAttribute("services", servicesService.findAllByCompanyId(companyId))
        return "company"
    }
}