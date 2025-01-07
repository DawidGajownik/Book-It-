package d.gajownik.bookit.user

import d.gajownik.bookit.appointment.AppointmentService
import d.gajownik.bookit.company.CompanyService
import jakarta.transaction.Transactional
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Month
import java.util.*

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val appointmentService: AppointmentService,
    private val companyService: CompanyService
) {
    fun saveUser(user: User) {
        user.password = passwordEncoder.encode(user.password) // Hashuj hasło
        userRepository.save(user)
    }
    fun findAllByCompanyId(companyId: Long): List<User> {
        return userRepository.findAllByCompanyId(companyId)
    }
    fun findByUserId(userId: Long): Optional<User> {
        return userRepository.findById(userId)
    }
    fun getUser(): User {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val username: String = authentication.name
        val user = userRepository.findByUsername(username)
        return user
    }
    fun getMonthOccupancy(month: Int, year: Int, user: User) : MutableMap<Int,Int>{
        val appointments = appointmentService.findAllByEmployeeId(user.id).filter { s->s.startDateTime.year==year && s.startDateTime.monthValue==month}
        val map: MutableMap<Int,Int> = mutableMapOf()
        val company = companyService.findByUserId(user.id)
        val workingTimeInMinutes = java.time.Duration.between(company!!.workTimeStart, company.workTimeEnd).toMinutes().toInt()
        for (i in 0..<Month.of(month).length(year%4==0)) {
            val bookedTimeInMinutes =
                appointments
                .stream()
                .filter{ s -> s.startDateTime.dayOfMonth==i+1}
                .mapToInt{s -> java.time.Duration.between(s.startDateTime, s.endDateTime).toMinutes().toInt()}
                .sum()
            val occupancy = bookedTimeInMinutes*100/workingTimeInMinutes
            map[i + 1] = occupancy
        }
        return map
    }
}
