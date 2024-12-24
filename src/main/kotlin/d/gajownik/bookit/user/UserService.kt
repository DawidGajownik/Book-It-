package d.gajownik.bookit.user

import jakarta.transaction.Transactional
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
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
    fun getUser(): User? {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val username: String = authentication.name
        val user = userRepository.findByUsername(username)
        return user
    }
}
