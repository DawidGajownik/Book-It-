package d.gajownik.bookit.message

import d.gajownik.bookit.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Message(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var content: String = "",

    @ManyToOne
    var sender: User = User(),

    var timestamp: LocalDateTime = LocalDateTime.now(),

    var recipientType: RecipientType = RecipientType.USER,

    var recipientId: Long = 0
) {
    enum class RecipientType {
        USER, COMPANY
    }
}