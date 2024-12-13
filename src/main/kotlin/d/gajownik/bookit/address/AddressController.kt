package d.gajownik.bookit.address

import org.springframework.context.MessageSource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class AddressController (
    val addressService: AddressService,
    var messageSource: MessageSource
) {
    @GetMapping("/process")
    fun processAddress(@RequestParam address: String, locale: Locale): String {
        val addressString = addressService.findDataFromString(address, locale).toString()
        if (addressString == "null"){
            return messageSource.getMessage("address.not.found", null, locale);
        }
        return messageSource.getMessage("address.confirmation", null, locale) + " " + addressString+"?"
    }
}