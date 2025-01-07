package d.gajownik.bookit.appointment

import d.gajownik.bookit.service.ServicesService
import d.gajownik.bookit.user.User
import d.gajownik.bookit.user.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.*
import java.time.temporal.ChronoField
import java.util.*


@Controller
class AppointmentController(
    private val messageSource: MessageSource,
    private val servicesService: ServicesService,
    private val appointmentService: AppointmentService,
    private val userService: UserService,
){
    @GetMapping("service/{serviceId}/month/{year}/{month}")
    fun month(model: Model, locale: Locale, @PathVariable year: Int, @PathVariable month: Int, @PathVariable serviceId: Long): String {
        val serviceOptional = servicesService.findById(serviceId)
        val service = serviceOptional.get()
        val appointments = appointmentService.findAllByServiceId(serviceId).filter { s->s.startDateTime.year==year && s.startDateTime.monthValue==month}

        model.addAttribute("occupation", servicesService.getMonthOccupancy(month, year, service, appointments))
        model.addAttribute("serviceId", serviceId)
        model.addAttribute("year", year)
        model.addAttribute("month", month-1)
        model.addAttribute("monthName",messageSource.getMessage(Month.of(month).name.lowercase(), null, locale))
        return "month"
    }


    @GetMapping("service/{serviceId}/week/{year}/{week}")
    fun week(@PathVariable year: Int, @PathVariable week: Int, model: Model, locale: Locale, @PathVariable serviceId: Long): String {

        val serviceOptional = servicesService.findById(serviceId)
        val service = serviceOptional.get()
        val availableHours = servicesService.getAvailableHours(service)
        val selectedDate: LocalDate = LocalDate.of(year, 1, 1).plusWeeks(week.toLong()-1)
        val weekStart: LocalDate = selectedDate.with(ChronoField.DAY_OF_WEEK, 1) // Poniedziałek
        val weekEnd: LocalDate = weekStart.plusDays(6) // Niedziela
        val appointments = appointmentService.findAllByServiceId(serviceId)

        if (week<1){
            return "redirect:service/${serviceId}/week/$year/1"
        }
        if (weekEnd.year>year){
            return "redirect:service/${serviceId}/week/${weekEnd.year}/1"
        }

        model.addAttribute("occupation", servicesService.getWeekOccupancy(availableHours, service, appointments, weekStart))
        model.addAttribute("serviceId", serviceId)
        model.addAttribute("year", year)
        model.addAttribute("weekStart", weekStart)
        model.addAttribute("weekEnd", weekEnd)
        model.addAttribute("weekDays", appointmentService.getWeekDays(weekStart, availableHours, messageSource, locale))
        model.addAttribute("week", week)

        return "week"
    }

    @GetMapping("service/{serviceId}/day/{year}/{month}/{day}")
    fun day(model: Model, locale: Locale, @PathVariable year: Int, @PathVariable month: Int, @PathVariable day: Int, @PathVariable serviceId: Long): String {

        val serviceOptional = servicesService.findById(serviceId)
        val service = serviceOptional.get()
        val availableHours = servicesService.getAvailableHours(service)
        val appointments = appointmentService.findAllByServiceId(serviceId).filter { s -> s.startDateTime.toLocalDate().equals(LocalDate.of(year, month, day)) }

        model.addAttribute("serviceName",servicesService.findById(serviceId).get().name)
        model.addAttribute("occupation",servicesService.getHourlyOccupancy(availableHours, service, appointments, LocalDate.of(year, month, day),null, null))
        model.addAttribute("serviceId", serviceId)
        model.addAttribute("week", appointmentService.getWeekNumber(year, month, day))
        model.addAttribute("year", year)
        model.addAttribute("monthName", messageSource.getMessage(Month.of(month).name.lowercase(), null, locale))
        model.addAttribute("month", month)
        model.addAttribute("day", day)
        return "day"
    }

    @GetMapping("service/{serviceId}/time/{year}/{month}/{day}/{hour}")
    fun finalBooking (model: Model, @PathVariable serviceId: Long, @PathVariable year: Int, @PathVariable month: Int, @PathVariable day: Int, @PathVariable hour: String, request: HttpServletRequest): String {
        val service = servicesService.findById(serviceId).get()
        val startDateTime = LocalDateTime.of(LocalDate.of(year,month,day), LocalTime.parse(hour))
        val duration = service.duration
        val endDateTime = startDateTime.plusMinutes(duration.toLong())

        request.session.setAttribute("linkWithHour", request.requestURI.toString())
        model.addAttribute("users", servicesService.getAvailableUsers(service, startDateTime, endDateTime, duration))
        model.addAttribute("selectedTime", hour)
        model.addAttribute("monthName",Month.of(month).name.lowercase())
        model.addAttribute("service", service)
        model.addAttribute("appointment", Appointment())
        return "final-reservation"
    }

    @PostMapping("/confirm")
    fun confirmBooking (@ModelAttribute appointment: Appointment, @RequestParam link: String, model: Model): String {
        appointment.client = userService.getUser()!!
        appointment.endDateTime=appointment.startDateTime.plusMinutes(servicesService.findById(appointment.service.id).get().duration.toLong())
        if (appointment.employee.id == 0L){
            appointment.employee.id=setEmployee(appointment)
        }
        appointmentService.save(appointment)
        return "redirect:$link"
    }
    fun setEmployee(appointment: Appointment): Long {
        val service = servicesService.findById(appointment.service.id).get()
        val employees = service.users
        var chosenEmployee = employees.random()
        val date = appointment.startDateTime.toLocalDate()
        val chosenEmployeeOccupancy = getEmployeeDayOccupancy(chosenEmployee, date)

        for (employee in employees) {
            if (getEmployeeDayOccupancy(employee, date)<chosenEmployeeOccupancy){
                chosenEmployee = employee
            }
        }
        return chosenEmployee.id
    }
    fun getEmployeeDayOccupancy(employee: User, date: LocalDate): Int {
        val company = employee.company
        var workDuration = 0
        if (company!=null){
            workDuration = Duration.between(company.workTimeStart, company.workTimeEnd).toMinutes().toInt()
        }
        val employeeAppointmentsThisDay = appointmentService.findAllByEmployeeIdAndDay(employee.id, date)
        val takenTime = employeeAppointmentsThisDay.sumOf { s -> Duration.between(s.startDateTime, s.endDateTime).toMinutes() }.toInt()
        if (takenTime==0){
            return 0
        }
        return workDuration/takenTime
    }
}