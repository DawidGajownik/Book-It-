package d.gajownik.bookit.company

import d.gajownik.bookit.appointment.AppointmentService
import d.gajownik.bookit.industry.IndustryService
import d.gajownik.bookit.service.ServicesService
import d.gajownik.bookit.user.User
import d.gajownik.bookit.user.UserService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.Locale


@Controller
class CompanyController(
    private val servicesService: ServicesService,
    private val userService: UserService,
    private val companyService: CompanyService,
    private val appointmentService: AppointmentService,
    private val messageSource: MessageSource
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

        val formattedWorkTimeStart: String = company.workTimeStart.format(DateTimeFormatter.ofPattern("HH:mm"))
        val formattedWorkTimeEnd: String = company.workTimeEnd.format(DateTimeFormatter.ofPattern("HH:mm"))
        model.addAttribute("workTimeStart", formattedWorkTimeStart)
        model.addAttribute("workTimeEnd", formattedWorkTimeEnd)

        model.addAttribute("company", company)
        model.addAttribute("roles", user.roles)
        model.addAttribute("users", users)
        model.addAttribute("services", servicesService.findAllByCompanyId(company.id))
        return "dashboard-company"
    }
    @GetMapping("/employee-dashboard")
    fun employeeDashboard(model: Model, locale: Locale): String {

        val date = LocalDateTime.now()
        val month = date.monthValue
        val year = date.year

        model.addAttribute("year", year)
        model.addAttribute("month", month-1)
        model.addAttribute("monthName",messageSource.getMessage(Month.of(month).name.lowercase(), null, locale))

        val user = userService.getUser()
        val appointments = appointmentService.findAllByEmployeeId(user.id).filter { s->s.startDateTime.year==year && s.startDateTime.monthValue==month}
        //model.addAttribute("occupation",appointmentService.getMonthOccupancy(month, year, service, appointments))
        val company = companyService.findById(user.company!!.id)
        val users = userService.findAllByCompanyId(company.get().id)
        model.addAttribute("roles", user.roles)
        model.addAttribute("users", users)
        model.addAttribute("services", servicesService.findAllByCompanyId(company.get().id))
        return "dashboard-employee"
    }
}