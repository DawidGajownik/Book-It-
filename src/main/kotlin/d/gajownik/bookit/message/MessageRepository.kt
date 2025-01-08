package d.gajownik.bookit.message

import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository : JpaRepository<Message, Long> {
    fun save(message: Message)
    fun getMessagesByRecipientTypeAndRecipientId(recipientType: Message.RecipientType, recipientId: Long): List<Message>
}