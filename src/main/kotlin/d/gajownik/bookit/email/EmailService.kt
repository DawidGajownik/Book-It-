package d.gajownik.bookit.email

import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component

@Component
class EmailService (
    private val javaMailSender: JavaMailSender
){
    @Throws(MessagingException::class)
    fun sendMessage(to: String, subject: String, text: String) {
        val message: MimeMessage = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)
        helper.setFrom("dawidgajownik6@gmail.com")
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(text, true)
        javaMailSender.send(message)
    }
}