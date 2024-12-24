package d.gajownik.bookit.address

import com.fasterxml.jackson.databind.ObjectMapper
import d.gajownik.bookit.APIConfig
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URL
import java.net.URLEncoder
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Service
@Transactional
class AddressService(
    private val addressRepository: AddressRepository,
){
    private val APIKEY = APIConfig.API_KEY
    @Throws(IOException::class)
    fun findData(address: Address, locale: Locale): Address? {
        val mapper = ObjectMapper()
        val url =
            URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + googleMapsApiAddressQuery(address) + "&key=" + APIKEY)
        return getAddressData(address, mapper, url, locale)
    }

    @Throws(IOException::class)
    fun findDataFromString(addressString: String?, locale: Locale): Address? {
        if (addressString.isNullOrEmpty()) {
            return null
        }
        val address = Address()
        val mapper = ObjectMapper()
        val url = URL(
            "https://maps.googleapis.com/maps/api/geocode/json?address=" + addressString.replace(
                " ".toRegex(),
                "%20"
            ) + "&key=" + APIKEY
        )
        return getAddressData(address, mapper, url, locale)
    }

    private fun getAddressData(address: Address, mapper: ObjectMapper, url: URL, locale: Locale): Address? {
        try {
            val language = locale.language // Pobieranie języka z Locale
            val localizedUrl = URL("${url}&language=$language") // Dodanie parametru language
            val jsonNode = mapper.readTree(localizedUrl)
            val node = jsonNode["results"][0]
            address.latitude=(node["geometry"]["location"]["lat"].asDouble())
            address.longitude=(node["geometry"]["location"]["lng"].asDouble())
            val addressComponents = node["address_components"]
            var i = 0
            while (addressComponents[i] != null) {
                var j = 0
                while (addressComponents[i]["types"][j] != null) {
                    val type = addressComponents[i]["types"][j].toString().replace("\"".toRegex(), "")
                    if (type == "administrative_area_level_1") {
                        address.administrative_area_level_1=(
                            addressComponents[i]["long_name"].toString().replace("\"".toRegex(), "")
                        )
                    }
                    if (type == "administrative_area_level_2") {
                        address.administrative_area_level_2=(
                            addressComponents[i]["long_name"].toString().replace("\"".toRegex(), "")
                        )
                    }
                    if (type == "country") {
                        address.country=(addressComponents[i]["long_name"].toString().replace("\"".toRegex(), ""))
                    }
                    if (type == "sublocality_level_1  ") {
                        address.district=(addressComponents[i]["long_name"].toString().replace("\"".toRegex(), ""))
                    }
                    if (type == "subpremise") {
                        address.apartmentNumber=(
                            addressComponents[i]["long_name"].toString().replace("\"".toRegex(), "")
                        )
                    }
                    if (type == "street_number") {
                        address.streetNumber=(
                            addressComponents[i]["long_name"].toString().replace("\"".toRegex(), "")
                        )
                    }
                    if (type == "route") {
                        address.street=(addressComponents[i]["long_name"].toString().replace("\"".toRegex(), ""))
                    }
                    if (type == "postal_code") {
                        address.postalCode=(addressComponents[i]["long_name"].toString().replace("\"".toRegex(), ""))
                    }
                    if (type == "locality") {
                        address.city=(addressComponents[i]["long_name"].toString().replace("\"".toRegex(), ""))
                    }
                    j++
                }
                i++
            }
            return address
        } catch (e: IOException) {
            return null
        } catch (e: NullPointerException) {
            return null
        }
    }

    fun save(addressString: String, locale: Locale) {
        require(addressString.isNotBlank()) { "Address string cannot be blank" }

        val addressData = findDataFromString(addressString, locale)
            ?: throw IllegalArgumentException("Invalid address data from string: $addressString")

        addressRepository.save(addressData)
            ?: throw IllegalStateException("AddressRepository is not initialized")
    }

    @Throws(UnsupportedEncodingException::class)
    fun googleMapsApiAddressQuery(address: Address): String {
        val formattedAddress = StringBuilder()

        if (address.street != null && address.street.isNotEmpty()) {
            formattedAddress.append(URLEncoder.encode(address.street, "UTF-8"))
        }

        if (address.streetNumber != null && !address.streetNumber.isEmpty()) {
            formattedAddress.append("%20")
                .append(URLEncoder.encode(address.streetNumber, "UTF-8"))
        }

        if (address.city != null && !address.city.isEmpty()) {
            formattedAddress.append(",+")
                .append(URLEncoder.encode(address.city, "UTF-8"))
        }

        if (address.postalCode != null && !address.postalCode.isEmpty()) {
            formattedAddress.append(",+")
                .append(URLEncoder.encode(address.postalCode, "UTF-8"))
        }
        return formattedAddress.toString()
    }

    private fun haversine(lat1: Double?, lon1: Double?, lat2: Double?, lon2: Double): Double? {
        if (lat1 == null || lat2 == null) {
            return null
        }
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1!!)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return Math.round(6371.0 * c * 100.0) / 100.0
    }

    @Throws(IOException::class)
    fun distanceCalculate(addressFromDb: Address, typedAddress: String, locale: Locale): Double? {
        var addressLatitude: Double? = null
        var addressLongitude: Double? = null
        if (findDataFromString(typedAddress, locale) != null) {
            val address = findDataFromString(typedAddress, locale)
            addressLatitude = address?.latitude
            addressLongitude = address?.longitude
        }
        return haversine(addressLatitude, addressLongitude, addressFromDb.latitude, addressFromDb.longitude)
    }

    @Throws(IOException::class)
    fun distanceCalculateFromCoordinates(latitude: Double, longitude: Double, typedAddress: String, locale: Locale): Double? {
        var addressLatitude: Double? = null
        var addressLongitude: Double? = null
        if (findDataFromString(typedAddress, locale) != null) {
            val address = findDataFromString(typedAddress, locale)
            addressLatitude = address?.latitude
            addressLongitude = address?.longitude
        }
        return haversine(addressLatitude, addressLongitude, latitude, longitude)
    }
}