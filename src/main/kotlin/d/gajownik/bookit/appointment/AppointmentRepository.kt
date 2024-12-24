package d.gajownik.bookit.appointment

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.time.LocalDateTime

interface AppointmentRepository : JpaRepository<Appointment, Long> {
    fun findAllByServiceId(serviceId: Long): List<Appointment>
    fun findAllByEmployeeId(userId: Long): List<Appointment>
    fun findAllByEmployeeIdAndStartDateTimeAfterAndEndDateTimeBefore(userId: Long, dateFrom: LocalDateTime, dateTo: LocalDateTime): List<Appointment>
}