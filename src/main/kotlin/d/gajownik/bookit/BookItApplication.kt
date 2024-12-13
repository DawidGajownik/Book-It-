package d.gajownik.bookit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookItApplication

fun main(args: Array<String>) {
    runApplication<BookItApplication>(*args)
}

