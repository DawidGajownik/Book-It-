package d.gajownik.bookit.user

import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

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
}
