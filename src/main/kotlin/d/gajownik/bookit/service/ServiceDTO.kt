package d.gajownik.bookit.service

import java.math.BigDecimal

class ServiceDTO(service: Service) {
    var id: Long = 0
    var name: String = ""
    var description: String = ""
    var price: BigDecimal = BigDecimal.ZERO
    var duration: Int = 0
    var companyName: String = ""
    var companyImage: String? = null
    var address: String = ""
    var longitude: Double = 0.0
    var latitude: Double = 0.0

    init {
        id = service.id
        name = service.name
        description = service.description
        price = service.price
        duration = service.duration
        companyName = service.company?.name ?: ""
        companyImage = service.company?.base64Image
        val addressObject = service.company?.address
        longitude = addressObject?.longitude!!
        latitude = addressObject.latitude
        val street = addressObject.street
        val city = addressObject.city
        val country = addressObject.country
        address = "$street, $city, $country"
    }
}