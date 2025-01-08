package d.gajownik.bookit.message

import d.gajownik.bookit.user.User
import d.gajownik.bookit.user.UserService
import java.time.LocalDateTime

class MessageDTO (message: Message,
    user: User
) {

    var content: String = ""
    var sender: String = ""
    var amISender: Boolean = false
    var timestamp: LocalDateTime = LocalDateTime.now()

    init {
        content = message.content
        sender = message.sender.username
        amISender = message.sender.id==user.id
        timestamp = message.timestamp
    }
}