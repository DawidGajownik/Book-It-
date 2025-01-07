package d.gajownik.bookit.user

import com.sun.jdi.IntegerValue
import d.gajownik.bookit.address.AddressRepository
import d.gajownik.bookit.address.AddressService
import d.gajownik.bookit.appointment.AppointmentService
import d.gajownik.bookit.company.CompanyService
import d.gajownik.bookit.industry.IndustryService
import d.gajownik.bookit.token.JwtUtil
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.jvm.Throws

@Controller
class UserController(
    private val userService: UserService,
    private val industryService: IndustryService,
    private val addressService: AddressService,
    private val addressRepository: AddressRepository,
    private val appointmentService: AppointmentService,
    private val companyService: CompanyService
) {


    @GetMapping("/login")
    fun loginGet(@RequestParam link: String?, model: Model): String {
        model.addAttribute("link", link)
        return "login"
    }

    @GetMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): String {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null) {
            SecurityContextLogoutHandler().logout(request, response, authentication)
        }
        return "redirect:/login" // Przekierowanie na stronę logowania lub główną
    }

    @GetMapping("/signup")
    fun signupGet(model: Model): String {
        model.addAttribute("user", User())
        return "signup"
    }
    @PostMapping("/signup")
    fun signupPost(@ModelAttribute user: User): String {
        if (user.roles == ""){
            user.roles="ROLE_USER"
        }
        userService.saveUser(user)
        return "redirect:/signup-success"
    }
    @GetMapping("/provider-signup")
    fun providerSignupGet(model: Model, locale:Locale): String {
        model.addAttribute("user", User())
        model.addAttribute("industries", industryService.findAllAndTranslate(locale))
        return "signup-provider"
    }
    @PostMapping("/provider-signup")
    fun providerSignupPost(@ModelAttribute user: User, @RequestParam address: String, locale: Locale): String {
        val addressFull = addressService.findDataFromString(address, locale)
        val company = user.company
        if (company != null) {
            if (addressFull != null) {
                addressRepository.save(addressFull)
                company.address=addressFull
                companyService.save(company)
            }
            company.users.add(user)
        }
        user.roles="ROLE_PROVIDER, ROLE_USER, ROLE EMPLOYEE"
        userService.saveUser(user)
        return "redirect:/signup-success"
    }
    @GetMapping("signup-success")
    fun signupSuccess(): String {
        return "signup-success"
    }

    @GetMapping("/accept-invitation")
    @Throws(SignatureException::class)
    fun acceptInvitation (@RequestParam token: String, model: Model): String {
        val user = User()
        val claims = JwtUtil.parseToken(token)
        val email = claims.body["sub"] as String
        val role = claims.body["role"] as String
        val companyId = claims.body["company_id"] as Int
        val company = companyService.findById(companyId.toLong()).get()
        user.email = email
        user.roles = role
        user.company = company
        model.addAttribute("user", user)
        return "signup-invited-employee"
    }
    @GetMapping("employee-day/{year}/{month}/{day}")
    fun employeeDayGet(model: Model, locale:Locale, @PathVariable year: Int, @PathVariable month: Int, @PathVariable day: Int): String {
        val employee = userService.getUser()
        val company = userService.findByUserId(employee.id).get().company
        if (company != null) {
            model.addAttribute("appointments", appointmentService.getAppointmentsWithPositionMap(employee.id, year, month, day, company))
            model.addAttribute("company", company)
            model.addAttribute("openingHours", listOf(listOf(
                company.workTimeStart.hour,
                company.workTimeStart.minute),
                listOf(
                    company.workTimeEnd.hour),
                company.workTimeEnd.minute))
        }
        return "employee-day"
    }
}