package d.gajownik.bookit

import java.util.Properties
import java.io.FileInputStream

object APIConfig {
    val API_KEY: String by lazy {
        val properties = Properties()
        val propertiesFile = "src/main/kotlin/d/gajownik/bookit/key.properties" // Ścieżka do pliku
        try {
            FileInputStream(propertiesFile).use { input ->
                properties.load(input)
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to load API_KEY from $propertiesFile", e)
        }
        properties.getProperty("API_KEY") ?: throw IllegalArgumentException("API_KEY not found in $propertiesFile")
    }
}
