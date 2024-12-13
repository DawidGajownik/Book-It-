package d.gajownik.bookit.image

import d.gajownik.bookit.industry.IndustryRepository
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ImageController(private val industryRepository: IndustryRepository) {
//    @GetMapping("/images/industry/{id}")
//    fun getImage(@PathVariable id: Long): ResponseEntity<ByteArray> {
//        val industry = industryRepository.findById(id).orElseThrow()
//        val image: ByteArray = industry.base64Image
//        return ResponseEntity.ok()
//            .header(HttpHeaders.CONTENT_TYPE, "image/png") // Zmień typ, jeśli obraz ma inny format
//            .body(image)
//    }
}