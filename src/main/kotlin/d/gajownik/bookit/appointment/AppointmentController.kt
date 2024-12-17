package d.gajownik.bookit.appointment

import d.gajownik.bookit.service.DayAvailability
import d.gajownik.bookit.service.Service
import d.gajownik.bookit.service.ServicesService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.*


@Controller
class AppointmentController (
    private val messageSource: MessageSource,
    private val servicesService: ServicesService,
    private val appointmentService: AppointmentService,
){
    @GetMapping("{serviceId}/month/{year}/{month}")
    fun month(model: Model, locale: Locale, @PathVariable year: Int, @PathVariable month: Int, @PathVariable serviceId: Long): String {
        val serviceOptional = servicesService.findById(serviceId)
        val service = serviceOptional.get()
        val availableHours = getAvailableHours(service)
        val appointments = appointmentService.findAllByServiceId(serviceId).filter { s->s.startDateTime.year==year && s.startDateTime.monthValue==month}

        val map: MutableMap<Int,Int> = mutableMapOf()
        for (i in 0..<Month.of(month).length(year%4==0)) {
            val maxToday = service.users.size * service.places * availableHours.size
            val bookedAppointmentsToday = appointments.count { s -> s.startDateTime.dayOfMonth == i + 1 }
            val availability = bookedAppointmentsToday * 100 / maxToday
            map[i + 1] = availability
        }
        model.addAttribute("occupation",map)
        model.addAttribute("serviceId", serviceId)
        model.addAttribute("avaliability")
        model.addAttribute("year", year)
        model.addAttribute("month", month-1)
        model.addAttribute("monthName",messageSource.getMessage(Month.of(month).name.lowercase(), null, locale))
        return "month"
    }

    @GetMapping("/{serviceId}/week/{year}/{week}")
    fun week(@PathVariable year: Int, @PathVariable week: Int, model: Model, locale: Locale, @PathVariable serviceId: Long): String {

        val serviceOptional = servicesService.findById(serviceId)
        val service = serviceOptional.get()
        val availableHours = getAvailableHours(service)
        val selectedDate: LocalDate = LocalDate.of(year, 1, 1).plusWeeks(week.toLong()-1)
        val weekStart: LocalDate = selectedDate.with(ChronoField.DAY_OF_WEEK, 1) // Poniedziałek
        val weekEnd: LocalDate = weekStart.plusDays(6) // Niedziela

        if (week<1){
            return "redirect:/${serviceId}/week/$year/1"
        }
        if (weekEnd.year>year){
            return "redirect:/${serviceId}/week/${weekEnd.year}/1"
        }

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

        val appointments = appointmentService.findAllByServiceId(serviceId)
        val occupation: MutableList<Map<LocalTime,Int>> = mutableListOf()
        for (i in 0..6) {
            occupation.add(getHourlyOccupancy(availableHours, service, appointments, weekStart.plusDays(i.toLong()), i, weekStart))
        }

        model.addAttribute("occupation", occupation)
        model.addAttribute("serviceId", serviceId)
        model.addAttribute("year", year)
        model.addAttribute("weekStart", weekStart)
        model.addAttribute("weekEnd", weekEnd)
        model.addAttribute("weekDays", weekDays)
        model.addAttribute("week", week)

        return "week"
    }

    @GetMapping("{serviceId}/day/{year}/{month}/{day}")
    fun day(model: Model, locale: Locale, @PathVariable year: Int, @PathVariable month: Int, @PathVariable day: Int, @PathVariable serviceId: Long): String {

        val serviceOptional = servicesService.findById(serviceId)
        val service = serviceOptional.get()
        val availableHours = getAvailableHours(service)
        val appointments = appointmentService.findAllByServiceId(serviceId).filter { s -> s.startDateTime.toLocalDate().equals(LocalDate.of(year, month, day)) }

        model.addAttribute("serviceName",servicesService.findById(serviceId).get().name)
        model.addAttribute("occupation",getHourlyOccupancy(availableHours, service, appointments, LocalDate.of(year, month, day),null, null))
        model.addAttribute("serviceId", serviceId)
        model.addAttribute("week", getWeekNumber(year, month, day))
        model.addAttribute("year", year)
        model.addAttribute("monthName", messageSource.getMessage(Month.of(month).name.lowercase(), null, locale))
        model.addAttribute("month", month)
        model.addAttribute("day", day)
        return "day"
    }

    fun getAvailableHours(service: Service): MutableList<LocalTime> {
        val hourStart = service.company?.workTimeStart
        val hourEnd = service.company?.workTimeEnd
        var hourToAdd = LocalTime.of(hourStart?.hour ?: 9, hourStart?.minute ?: 30)
        val availableHours = mutableListOf<LocalTime>()
        val step = service.duration //bookowanie możliwe co godzine
        if (hourToAdd != null) {
            while (hourToAdd.plusMinutes(service.duration.toLong())<=(hourEnd)) {
                availableHours.add(hourToAdd)
                hourToAdd = hourToAdd.plusMinutes(step.toLong())
            }
        }
        return availableHours
    }

    fun getHourlyOccupancy(availableHours: MutableList<LocalTime>, service: Service, appointments: List<Appointment>, date: LocalDate, i: Int?, weekStart: LocalDate?): Map <LocalTime, Int> {
        val map:MutableMap<LocalTime, Int> = mutableMapOf()
        for (element in availableHours) {
            var availability = 0
            var occupancyToReduce =0
            val maxThisHour = service.users.size*service.places
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
                if (!appointmentService.isEmployeeAvailable(user, LocalDateTime.of(date, element), LocalDateTime.of(date, element.plusMinutes(service.duration.toLong())), service.duration, service)) {
                    occupancyToReduce+=service.places
                }
            }
            val bookedThisHour = bookedAppointmentsThisHour.count()+occupancyToReduce
            availability = bookedThisHour*100/maxThisHour
            map[element] = availability
        }
        return map
    }

    fun getWeekNumber(year: Int, month: Int, day: Int): Int {
        val date = LocalDate.of(year, month, day)
        val startOfYear = LocalDate.of(year, 1, 1)
        val pastDaysOfYear: Long = ChronoUnit.DAYS.between(startOfYear, date)-1
        val firstDayOfWeek = startOfYear.dayOfWeek.value // 1 (Poniedziałek) - 7 (Niedziela)
        return ((pastDaysOfYear + firstDayOfWeek) / 7.0).toInt()+1
    }
}