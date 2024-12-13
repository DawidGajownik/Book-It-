package d.gajownik.bookit.industry

import d.gajownik.bookit.GoogleTranslate
import org.springframework.stereotype.Service
import java.util.*

@Service
class IndustryService (
    private val industryRepository: IndustryRepository,
    private val googleTranslate: GoogleTranslate
){
    fun findAll(): List<Industry>{
        return industryRepository.findAll()
    }
    fun findById(id: Long): Industry {
        return industryRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Industry with ID $id not found") }
    }
    fun findAllAndTranslate(locale: Locale): MutableList<Industry?>? {
        if (industryRepository.findAll().size==0){
            return industryRepository.findAll()
        }
        val industries = industryRepository.findAll().stream().map(this::copyCategory).toList()

        val query: String = industries.joinToString("%") { it?.name.toString() }
        val translated: List<String> = googleTranslate.translate(query, locale).split("%")

        for (i in translated.indices) {
            industries.get(i)?.name =translated[i]
        }

        return industries
    }
    fun copyCategory(industry: Industry): Industry? {
        val id = industry.id
        return industry.name?.let { industry.description?.let { it1 -> Industry(industry.id, it, it1) } }
    }
}