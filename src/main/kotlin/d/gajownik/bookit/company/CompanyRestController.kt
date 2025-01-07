package d.gajownik.bookit.company

import d.gajownik.bookit.email.EmailService
import d.gajownik.bookit.token.JwtUtil
import d.gajownik.bookit.user.UserService
import io.jsonwebtoken.security.SignatureException
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.Throws

@RestController
class CompanyRestController(
    private val userService: UserService,
    private val companyService: CompanyService,
    private val emailService: EmailService
) {
    @GetMapping("/invite")
    fun invite (@RequestParam email: String, @RequestParam role: String) {
        val user = userService.getUser()
        val company = companyService.findByUserId(user.id)
        val token = JwtUtil.generateToken(email, role, company!!.id)
        val invitationLink = "http://localhost:8080/accept-invitation?token=$token"
        emailService.sendMessage(email, "invitation to company", invitationLink)
        println(email)
        println(role)
    }
}