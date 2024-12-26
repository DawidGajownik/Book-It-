package d.gajownik.bookit.appointment

import d.gajownik.bookit.company.Company
import d.gajownik.bookit.service.DayAvailability
import d.gajownik.bookit.user.User
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
class AppointmentService (
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
    fun getAvailableUsers(service: d.gajownik.bookit.service.Service, startDateTime: LocalDateTime, endDateTime: LocalDateTime, duration: Int): Map<User, Int> {
         return service.users
            .stream()
            .filter{ s->isEmployeeAvailable(s, startDateTime, endDateTime, duration, service) }
            .toList()
            .associateWith { freePlaces(it, service.places, startDateTime) }
    }
    fun getAvailableHours(service: d.gajownik.bookit.service.Service): MutableList<LocalTime> {
        val hourStart = service.company?.workTimeStart
        val hourEnd = service.company?.workTimeEnd
        var hourToAdd = LocalTime.of(hourStart?.hour ?: 9, hourStart?.minute ?: 30)
        val availableHours = mutableListOf<LocalTime>()
        val step = service.duration
        if (hourToAdd != null) {
            while (hourToAdd.plusMinutes(service.duration.toLong())<=(hourEnd)&&hourToAdd.plusMinutes(service.duration.toLong())>=(hourStart)) {
                availableHours.add(hourToAdd)
                hourToAdd = hourToAdd.plusMinutes(step.toLong())
            }
        }
        return availableHours
    }

    fun getHourlyOccupancy(availableHours: MutableList<LocalTime>, service: d.gajownik.bookit.service.Service, appointments: List<Appointment>, date: LocalDate, i: Int?, weekStart: LocalDate?): Map <LocalTime, Int> {
        val map:MutableMap<LocalTime, Int> = mutableMapOf()
        val maxThisHour = service.users.size*service.places
        for (element in availableHours) {
            var availability = 0
            var occupancyToReduce =0
            var bookedAppointmentsThisHour = appointments
                .filter { s ->
                    element == LocalTime.of(
                        s.startDateTime.hour,
                        s.startDateTime.minute
                    )
                }
            for (user in service.users) {
                if (i!=null) {
                    if (weekStart != null) {
                        bookedAppointmentsThisHour = bookedAppointmentsThisHour.filter { s ->
                            weekStart.plusDays(i.toLong()) == s.startDateTime.toLocalDate()
                        }
                    }
                }
                if (isEmployeeScheduledSomewhereElse(user, LocalDateTime.of(date, element), LocalDateTime.of(date, element.plusMinutes(service.duration.toLong())), service.duration, service)) {
                    occupancyToReduce+=service.places
                }
            }
            val bookedThisHour = bookedAppointmentsThisHour.count()+occupancyToReduce
            availability = bookedThisHour*100/maxThisHour
            map[element] = availability
        }
        return map
    }

    fun getMonthOccupancy(month: Int, year: Int, service: d.gajownik.bookit.service.Service, appointments: List<Appointment>) : MutableMap<Int,Int>{
        val map: MutableMap<Int,Int> = mutableMapOf()
        val availableHours = getAvailableHours(service)
        for (i in 0..<Month.of(month).length(year%4==0)) {
            val hourlyOccupancy = getHourlyOccupancy(availableHours, service, appointments.stream().filter { s -> s.startDateTime.dayOfMonth==i+1}.toList(), LocalDate.of(year, month, i+1), null, null)
            val sum = hourlyOccupancy.values.stream().mapToInt { j:Int -> j }.sum()
            val divisor = hourlyOccupancy.values.stream().count().toInt()
            val occupancy = sum/divisor
            map[i + 1] = occupancy
        }
        return map
    }

    fun getWeekOccupancy(availableHours: MutableList<LocalTime>, service: d.gajownik.bookit.service.Service, appointments: List<Appointment>, weekStart: LocalDate): MutableList<Map<LocalTime, Int>> {
        val occupation: MutableList<Map<LocalTime,Int>> = mutableListOf()
        for (i in 0..6) {
            occupation.add(getHourlyOccupancy(availableHours, service, appointments, weekStart.plusDays(i.toLong()), i, weekStart))
        }
        return occupation
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
    fun getAppointmentsWithPositionMap(employeeId: Long, year: Int, month: Int, day: Int, company: Company): MutableMap<Appointment, List<Int>>{
        val appointments = findAllByEmployeeIdAndDay(employeeId, LocalDate.of(year, month, day))
        val appointmentsWithPositionMap = mutableMapOf<Appointment, List<Int>>()
        for (appointment in appointments) {
            val top = 60*(appointment.startDateTime.hour-company.workTimeStart.hour)+(appointment.startDateTime.minute-company.workTimeStart.minute)+18
            val height = (60*(appointment.endDateTime.hour-appointment.startDateTime.hour))+(appointment.endDateTime.minute-appointment.startDateTime.minute)
            appointmentsWithPositionMap[appointment] = listOf(top, height)
        }
        return appointmentsWithPositionMap
    }
}