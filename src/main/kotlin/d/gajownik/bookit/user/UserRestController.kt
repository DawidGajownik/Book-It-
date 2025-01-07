package d.gajownik.bookit.user

import d.gajownik.bookit.appointment.AppointmentDTO
import d.gajownik.bookit.appointment.AppointmentService
import d.gajownik.bookit.company.CompanyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class UserRestController(
    private val userService: UserService,
    private val appointmentService: AppointmentService,
    private val companyService: CompanyService
){
    @GetMapping("/employee-dashboard-occupancy")
    fun employeeDashboard(@RequestParam month: Int, @RequestParam year: Int): Map<Int,Int> {
        return userService.getMonthOccupancy(month, year, userService.getUser())
    }
    @GetMapping("/employee-day")
    fun getEmployeeDay(@RequestParam year: Int, month: Int, day: Int): List<List<Any>> {
        val employee = userService.getUser()
        val company = userService.findByUserId(employee.id).get().company
        if (company != null) {
            return appointmentService.getAppointmentsWithPosition(employee, year, month+1, day, company)
        }
        return listOf()
    }
}