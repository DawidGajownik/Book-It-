package d.gajownik.bookit

import d.gajownik.bookit.company.CompanyService
import d.gajownik.bookit.industry.IndustryService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.util.*

@Controller
class HomeController(
    private val industryService: IndustryService) {

    @GetMapping("/")
    fun home(model: Model, locale: Locale): String {
        model.addAttribute("industries", industryService.findAllAndTranslate(locale))
        return "index"
    }

//    @GetMapping("/language")
//    fun changeLanguage(
//        @RequestParam lang: String,
//        @RequestParam redirectUrl: String,
//        request: HttpServletRequest
//    ): String {
//        val localeResolver = RequestContextUtils.getLocaleResolver(request)
//            ?: throw IllegalStateException("LocaleResolver not found")
//
//        val locale = Locale(lang)
//        println("Setting language to: $locale")
//        localeResolver.setLocale(request, null, locale)
//        println("Locale successfully set to: $locale")
//
//        return "redirect:$redirectUrl"
//    }
}
