package d.gajownik.bookit

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder() // Używa BCrypt do szyfrowania i weryfikacji
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers( "/", "/css/**", "/js/**", "/images/**", "/signup", "/provider-signup", "/process", "/service/**", "/services/**", "/filter-services*", "/pages").permitAll() // Publiczne strony
                    .anyRequest().authenticated() // Wszystkie inne wymagają zalogowania
            }
            .formLogin { form ->
                form
                    .loginPage("/login")
                    .successHandler { request, response, authentication ->
                        val url = request.session.getAttribute("linkWithHour")
                        request.session.removeAttribute("linkWithHour")
                        var urlString = "/"
                        if (url != null) {
                            urlString=url.toString()
                        }
                        response.sendRedirect(urlString)
                    }
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .permitAll()
            }
        return http.build()
    }
}
