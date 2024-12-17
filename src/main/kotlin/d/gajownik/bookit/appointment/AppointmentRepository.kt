package d.gajownik.bookit.appointment

import org.springframework.data.jpa.repository.JpaRepository

interface AppointmentRepository : JpaRepository<Appointment, Long> {
    fun findAllByServiceId(serviceId: Long): List<Appointment>
    fun findAllByEmployeeId(userId: Long): List<Appointment>
}