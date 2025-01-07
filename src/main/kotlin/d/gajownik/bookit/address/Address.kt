package d.gajownik.bookit.address

import jakarta.persistence.*

@Entity
data class Address (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var street: String = "",
    var streetNumber: String = "",
    var apartmentNumber: String = "",
    var district: String = "",
    var city: String = "",
    var administrative_area_level_2: String = "",
    var administrative_area_level_1: String = "",
    var country: String = "",
    var postalCode: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0

){
    override fun toString(): String {
        return city+", "+postalCode+", "+administrative_area_level_1+", "+administrative_area_level_2+", "+country
    }
    fun getAddressAsString(): String {
        return toString()
    }
}