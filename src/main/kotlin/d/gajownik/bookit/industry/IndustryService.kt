package d.gajownik.bookit.industry

import d.gajownik.bookit.GoogleTranslate
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class IndustryService (
    private val industryRepository: IndustryRepository,
    private val googleTranslate: GoogleTranslate
){

    fun findAllAndTranslate(locale: Locale): MutableList<Industry?>? {

        if (industryRepository.findAll().size==0){
            return industryRepository.findAll()
        }

        val industries = industryRepository.findAll().stream().map(this::copy).toList()
        val queryNames: String = industries.joinToString("%%") { it?.name.toString() }
        val queryDescriptions = industries.joinToString("%%") { it?.description.toString() }
        val query = "$queryNames%%$queryDescriptions"
        val translated: List<String> = googleTranslate.translate(query, locale, "en").split("%%")
        val transNames = translated.subList(0, translated.size/2)
        val transDescriptions = translated.subList(translated.size/2, translated.size)

        for (i in transNames.indices) {
            industries[i]?.name =transNames[i]
            industries[i]?.description = transDescriptions[i]
        }
        return industries
    }

    fun copy(industry: Industry): Industry? {
        return industry.name?.let { industry.description?.let { it1 -> Industry(industry.id, it, it1) } }
    }
}