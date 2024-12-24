package d.gajownik.bookit.company

import d.gajownik.bookit.service.ServicesService
import d.gajownik.bookit.user.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class CompanyController(
    private val servicesService: ServicesService,
    private val userService: UserService,
    private val companyService: CompanyService
){
    @GetMapping("/dashboard")
    fun company(model: Model): String {
        val user = userService.getUser()
        val company = companyService.findByUserId(user!!.id)
        model.addAttribute("services", servicesService.findAllByCompanyId(company.id))
        return "company"
    }
}