package d.gajownik.bookit.company

import d.gajownik.bookit.appointment.AppointmentService
import d.gajownik.bookit.industry.IndustryService
import d.gajownik.bookit.service.ServicesService
import d.gajownik.bookit.user.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.time.format.DateTimeFormatter
import java.util.Locale


@Controller
class CompanyController(
    private val servicesService: ServicesService,
    private val userService: UserService,
    private val companyService: CompanyService,
    private val appointmentService: AppointmentService,
    private val industryService: IndustryService
){
    @GetMapping("/dashboard")
    fun company(model: Model): String {
        val user = userService.getUser()
        if (user.roles.contains("PROVIDER")) {
            return "redirect:/company-dashboard"
        }
        if (user.roles.contains("MODERATOR")) {
            return "redirect:/moderator-dashboard"
        }
        if (user.roles.contains("EMPLOYEE")) {
            return "redirect:/employee-dashboard"
        }
        return "redirect:/user-dashboard"
    }
    @GetMapping("/company-dashboard")
    fun companyDashboard(model: Model, locale: Locale): String {
        val user = userService.getUser()
        val company = companyService.findById(user.company!!.id).get()
        val users = userService.findAllByCompanyId(company.id)
        val industries = industryService.findAllAndTranslate(locale)

        val formattedWorkTimeStart: String = company.workTimeStart.format(DateTimeFormatter.ofPattern("HH:mm"))
        val formattedWorkTimeEnd: String = company.workTimeEnd.format(DateTimeFormatter.ofPattern("HH:mm"))
        model.addAttribute("workTimeStart", formattedWorkTimeStart)
        model.addAttribute("workTimeEnd", formattedWorkTimeEnd)

        //model.addAttribute("industries", industries)
        model.addAttribute("company", company)
        model.addAttribute("roles", user.roles)
        model.addAttribute("users", users)
        model.addAttribute("services", servicesService.findAllByCompanyId(company.id))
        return "dashboard-company"
    }
    @GetMapping("/employee-dashboard")
    fun employeeDashboard(model: Model): String {
        val user = userService.getUser()
        val company = companyService.findById(user.company!!.id)
        val users = userService.findAllByCompanyId(company.get().id)
        model.addAttribute("roles", user.roles)
        model.addAttribute("users", users)
        model.addAttribute("services", servicesService.findAllByCompanyId(company.get().id))
        return "dashboard-employee"
    }
}