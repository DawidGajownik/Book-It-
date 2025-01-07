package d.gajownik.bookit.appointment

import d.gajownik.bookit.company.Company
import d.gajownik.bookit.service.DayAvailability
import d.gajownik.bookit.service.ServiceDTO
import d.gajownik.bookit.service.ServicesService
import d.gajownik.bookit.user.User
import d.gajownik.bookit.user.UserService
import jakarta.transaction.Transactional
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.temporal.ChronoUnit
import java.util.*

@Service
@Transactional
class AppointmentService(
    val appointmentRepository: AppointmentRepository
) {
    fun findAllByServiceId(serviceId: Long): List<Appointment> {
        return appointmentRepository.findAllByServiceId(serviceId)
    }
    fun freePlaces(employee: User, places: Int, startDateTime: LocalDateTime): Int {
        return places - appointmentRepository.countAllByEmployeeIdAndStartDateTime(employee.id, startDateTime)
    }
    fun appointmentsForRequestedEmployeeAndRequestedTime(employee: User, start: LocalDateTime, end: LocalDateTime, duration: Int, service: d.gajownik.bookit.service.Service): List<Appointment> {
        return appointmentRepository.findAllByEmployeeId(employee.id)
            .filter { s-> !s.startDateTime.isAfter(start.plusMinutes(duration.toLong()).minusNanos(1)) && !s.endDateTime.isBefore(start) && s.endDateTime!=start}
    }
    fun isThereStillPlaceForThisAppointment(employee: User, start: LocalDateTime, end: LocalDateTime, duration: Int, service: d.gajownik.bookit.service.Service): Boolean {
        return service.places != appointmentsForRequestedEmployeeAndRequestedTime(employee, start, end, duration, service).filter{s -> s.service == service}.size
    }
    fun isEmployeeAvailable(employee: User, start: LocalDateTime, end: LocalDateTime, duration: Int, service: d.gajownik.bookit.service.Service): Boolean {
        return !isEmployeeScheduledSomewhereElse(employee, start, end, duration, service) && isThereStillPlaceForThisAppointment(employee, start, end, duration, service)
    }
    fun isEmployeeScheduledSomewhereElse(employee: User, start: LocalDateTime, end: LocalDateTime, duration: Int, service: d.gajownik.bookit.service.Service): Boolean {
        return appointmentsForRequestedEmployeeAndRequestedTime(employee,start, end, duration, service).any { s -> s.service != service }
    }
    fun findAllByEmployeeIdAndDay(employeeId: Long, date: LocalDate): List<Appointment> {
        return appointmentRepository.findAllByEmployeeIdAndStartDateTimeAfterAndEndDateTimeBefore(employeeId, LocalDateTime.of(date, LocalTime.of(0,0)), LocalDateTime.of(date.plusDays(1), LocalTime.of(0,0)))
    }
    fun save (appointment: Appointment) {
        appointmentRepository.save(appointment)
    }


    fun getWeekNumber(year: Int, month: Int, day: Int): Int {
        val date = LocalDate.of(year, month, day)
        val startOfYear = LocalDate.of(year, 1, 1)
        val pastDaysOfYear: Long = ChronoUnit.DAYS.between(startOfYear, date)-1
        val firstDayOfWeek = startOfYear.dayOfWeek.value
        return ((pastDaysOfYear + firstDayOfWeek) / 7.0).toInt()+1
    }

    fun getWeekDays(weekStart: LocalDate, availableHours: MutableList<LocalTime>, messageSource: MessageSource, locale: Locale): MutableList<DayAvailability> {
        val weekDays: MutableList<DayAvailability> = ArrayList<DayAvailability>()
        for (i in 0..6) {
            val date: LocalDate = weekStart.plusDays(i.toLong())
            val dayAndMonth = StringBuilder()
            if (date.dayOfMonth.toString().length==1){
                dayAndMonth.append(0).append(date.dayOfMonth.toString())
            } else {
                dayAndMonth.append(date.dayOfMonth.toString())
            }
            dayAndMonth.append(".")
            if (date.monthValue.toString().length==1){
                dayAndMonth.append(0).append(date.monthValue.toString())
            } else {
                dayAndMonth.append(date.monthValue.toString())
            }
            weekDays.add(
                DayAvailability(
                    date.year,
                    date.monthValue,
                    date.dayOfMonth,
                    availableHours,
                    dayAndMonth.toString(),
                    messageSource.getMessage(date.dayOfWeek.name.lowercase(Locale.getDefault()), null, locale),
                    Month.of(date.monthValue).name.lowercase()
                )
            )
        }
        return weekDays
    }
    fun getAppointmentsWithPosition(user: User, year: Int, month: Int, day: Int, company: Company): List<List<Any>>{
        val result = findAllByEmployeeIdAndDay(user.id, LocalDate.of(year, month, day))
            .map { s: Appointment -> AppointmentDTO(s)  }
            .map { s-> listOf(s, listOf((60*(s.startDateTime.hour-company.workTimeStart.hour)+(s.startDateTime.minute-company.workTimeStart.minute)+18),(60*(s.endDateTime.hour-s.startDateTime.hour))+(s.endDateTime.minute-s.startDateTime.minute))) }
            .toList()
        val returnList = ArrayList<List<Any>>()
        return result
    }

    fun getAppointmentsWithPositionMap(user: User, year: Int, month: Int, day: Int, company: Company): MutableMap<AppointmentDTO, List<Int>>{
        val appointments = findAllByEmployeeIdAndDay(user.id, LocalDate.of(year, month, day))
            .map { s: Appointment -> AppointmentDTO(s)  }
            .toList()
        val appointmentsWithPositionMap = mutableMapOf<AppointmentDTO, List<Int>>()
        for (appointment in appointments) {
            val top = 60*(appointment.startDateTime.hour-company.workTimeStart.hour)+(appointment.startDateTime.minute-company.workTimeStart.minute)+18
            val height = (60*(appointment.endDateTime.hour-appointment.startDateTime.hour))+(appointment.endDateTime.minute-appointment.startDateTime.minute)
            appointmentsWithPositionMap[appointment] = listOf(top, height)
        }
        return appointmentsWithPositionMap
    }
    fun findAllByEmployeeId(employeeId: Long) : List<Appointment>{
        return appointmentRepository.findAllByEmployeeId(employeeId)
    }
}