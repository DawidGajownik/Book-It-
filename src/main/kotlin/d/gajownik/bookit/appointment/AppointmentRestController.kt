package d.gajownik.bookit.appointment

import d.gajownik.bookit.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RestController
class AppointmentRestController(
    private val appointmentService: AppointmentService,
    private val userService: UserService
) {
    @PostMapping ("/appointments-update")
    fun appointmentsUpdate(@RequestBody appointments: List<AppointmentUpdate>) : ResponseEntity<Any> {
        val employee = userService.getUser()
        println(employee.id)
        println(appointments)
        val date = LocalDate.of(appointments[0].year, appointments[0].month, appointments[0].day)
        for (appointment in appointments) {
            val split = appointment.label.split(" ")
            val newStartTime = LocalTime.of(split[split.size - 3].split(":")[0].toInt(), split[split.size - 3].split(":")[1].toInt())
            println(newStartTime)
            val newStartDateTime = LocalDateTime.of(date, newStartTime)
            val startDateTime = LocalDateTime.of(date, LocalTime.of(appointment.start.split(":")[0].toInt(), appointment.start.split(":")[1].toInt()))
            println(startDateTime)
            val updatedAppointment = appointmentService.getByEmployeeIdAndStartDateTime(employee.id, startDateTime)
            val newEndDateTime = newStartDateTime.plusMinutes(updatedAppointment.service.duration.toLong())
            updatedAppointment.startDateTime = newStartDateTime
            updatedAppointment.endDateTime = newEndDateTime
            appointmentService.update(updatedAppointment)
        }
        return ResponseEntity(appointments, HttpStatus.OK)
    }
}