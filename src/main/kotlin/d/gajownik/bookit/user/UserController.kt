package d.gajownik.bookit.user

import d.gajownik.bookit.address.AddressRepository
import d.gajownik.bookit.address.AddressService
import d.gajownik.bookit.industry.IndustryService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
class UserController(
    private val userService: UserService,
    private val industryService: IndustryService,
    private val addressService: AddressService,
    private val addressRepository: AddressRepository
) {


    @GetMapping("/login")
    fun loginGet(): String {
        return "login"
    }
    @PostMapping("/login")
    fun loginPost(): String {
        return "login"
    }

    @GetMapping("/signup")
    fun signupGet(model: Model): String {
        model.addAttribute("user", User())
        return "signup"
    }
    @PostMapping("/signup")
    fun signupPost(@ModelAttribute user: User): String {
        user.roles="ROLE_USER"
        userService.saveUser(user)
        return "signup"
    }

    @GetMapping("/provider-signup")
    fun providerSignupGet(model: Model, locale:Locale): String {
        model.addAttribute("user", User())
        model.addAttribute("industries", industryService.findAllAndTranslate(locale))
        return "provider-signup"
    }
    @PostMapping("/provider-signup")
    fun providerSignupPost(@ModelAttribute user: User, @RequestParam address: String, locale: Locale): String {
        val addressFull = addressService.findDataFromString(address, locale)
        if (addressFull != null) {
            addressRepository.save(addressFull)
            user.company.address=addressFull
        }

//        val industry = requireNotNull(user.company.industry) { "Industry cannot be null" }
//
//        val industryId = requireNotNull(industry.id) { "Industry ID cannot be null" }
//
//        user.company.industry = industryService.findById(industryId)


        user.roles="ROLE_PROVIDER, ROLE_USER"
        userService.saveUser(user)
        return "provider-signup"
    }
    @GetMapping("signup-success")
    fun signupSuccess(): String {
        return "signup-success"
    }
}