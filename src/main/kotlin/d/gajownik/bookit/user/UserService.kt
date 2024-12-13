package d.gajownik.bookit.user

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun saveUser(user: User) {

        user.password = passwordEncoder.encode(user.password) // Hashuj hasło
        userRepository.save(user)
    }
}
