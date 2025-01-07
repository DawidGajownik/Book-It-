package d.gajownik.bookit.appointment

import d.gajownik.bookit.user.User
import java.time.LocalDateTime

class AppointmentDTO (appointment: Appointment) {

    var id: Long = 0
    var service: String = ""
    var serviceId: Long = 0
    var client: String =""
    var clientId: Long = 0
    var startDateTime: LocalDateTime = LocalDateTime.now()
    var endDateTime: LocalDateTime = LocalDateTime.now()
    var startDateTimeFormatted: String = ""
    var endDateTimeFormatted: String =""

    init {
        id = appointment.id
        service = appointment.service.name
        serviceId = appointment.service.id
        client = appointment.client.username
        clientId = appointment.client.id
        startDateTime = appointment.startDateTime
        endDateTime = appointment.endDateTime
        startDateTimeFormatted = startDateTime.hour.toString()+":"+startDateTime.minute.toString().padStart(2, '0')
        endDateTimeFormatted = endDateTime.hour.toString()+":"+endDateTime.minute.toString().padStart(2, '0')
    }
}