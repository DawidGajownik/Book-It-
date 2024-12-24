package d.gajownik.bookit.appointment

import d.gajownik.bookit.user.User
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
@Transactional
class AppointmentService (
    val appointmentRepository: AppointmentRepository
) {
    fun findAllByServiceId(serviceId: Long): List<Appointment> {
        return appointmentRepository.findAllByServiceId(serviceId)
    }
    fun isEmployeeAvailable(employee: User, start: LocalDateTime, end: LocalDateTime, duration: Int, service: d.gajownik.bookit.service.Service): Boolean {
        val appointments = appointmentRepository.findAllByEmployeeId(employee.id)
            .filter { s-> !s.startDateTime.isAfter(start.plusMinutes(duration.toLong()).minusNanos(1)) && !s.endDateTime.isBefore(start) && s.endDateTime!=start}
            .filter { s-> s.service != service }
        return appointments.isEmpty()
    }
    fun findAllByEmployeeIdAndDay(employeeId: Long, date: LocalDate): List<Appointment> {
        return appointmentRepository.findAllByEmployeeIdAndStartDateTimeAfterAndEndDateTimeBefore(employeeId, LocalDateTime.of(date, LocalTime.of(0,0)), LocalDateTime.of(date.plusDays(1), LocalTime.of(0,0)))
    }
    fun save (appointment: Appointment) {
        appointmentRepository.save(appointment)
    }
}