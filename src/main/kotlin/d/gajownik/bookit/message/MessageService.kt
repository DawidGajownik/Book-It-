package d.gajownik.bookit.message

import d.gajownik.bookit.company.Company
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class MessageService(private val messageRepository: MessageRepository) {
    fun save (message : Message) {
        messageRepository.save(message)
    }
    fun getCompanyChat(recipientId: Long): List<Message> {
        val mes = messageRepository.getMessagesByRecipientTypeAndRecipientId(Message.RecipientType.COMPANY, recipientId)
        val mesList = mutableListOf<Message>()
        return mes
    }
}