package d.gajownik.bookit

import com.google.cloud.translate.Detection
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.google.cloud.translate.Translation
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Component
import java.util.*

@Component
@RequiredArgsConstructor
class GoogleTranslate {
    private val APIKEY = "AIzaSyBVEnKq5YxoW7wOQRCj_smmVYfgiIpfK0w"

    fun translate(text: String, language: Locale, sourceLanguage: String): String {
        val translate: Translate = TranslateOptions.newBuilder().setApiKey(APIKEY).build().service
        if (language.toString() == sourceLanguage) {
            return text
        }
        if (text.isEmpty()) {
            return ""
        }
        val translation: Translation = translate.translate(
            text,
            Translate.TranslateOption.sourceLanguage(sourceLanguage),
            Translate.TranslateOption.targetLanguage(language.language)
        )
        return translation.translatedText
    }

    fun translateWithDetection(text: String, language: Locale): String {
        val translate: Translate = TranslateOptions.newBuilder().setApiKey(APIKEY).build().service
        val detection: Detection = translate.detect(text)
        if (language.toString() == detection.language) {
            return text
        }
        if (text.isEmpty()) {
            return ""
        }
        val translation: Translation = translate.translate(
            text,
            Translate.TranslateOption.sourceLanguage(detection.language),
            Translate.TranslateOption.targetLanguage(language.language)
        )
        return translation.translatedText
    }
}
