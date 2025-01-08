
package d.gajownik.bookit.message

import d.gajownik.bookit.appointment.Appointment
import d.gajownik.bookit.appointment.AppointmentDTO
import d.gajownik.bookit.user.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class MessageRestController(
    private val userService: UserService,
    private val messageService: MessageService
) {
    @PostMapping("/send-company-message")
    fun sendMessage(@RequestBody message: String) {
        val newMessage = Message()
        newMessage.sender=userService.getUser()
        newMessage.content=message
        newMessage.timestamp= LocalDateTime.now()
        newMessage.recipientType=Message.RecipientType.COMPANY
        newMessage.recipientId=userService.getUser().company!!.id
        messageService.save(newMessage)
    }
    @GetMapping("/get-company-chat")
    fun getCompanyChat(): List<MessageDTO> {
        val user = userService.getUser()
        val mes = messageService.getCompanyChat(user.company!!.id).stream().map { s: Message -> MessageDTO(s, user)}.toList()
        val mes2 = messageService.getCompanyChat(user.company!!.id).stream().map { s: Message -> MessageDTO(s, user)}.toList()
        return mes
    }
}