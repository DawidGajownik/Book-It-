package d.gajownik.bookit.appointment

import d.gajownik.bookit.service.Service
import d.gajownik.bookit.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Appointment(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne
    var service: Service = Service(),

    @ManyToOne
    var client: User = User(),

    @ManyToOne
    var employee: User = User(),

    var startDateTime: LocalDateTime = LocalDateTime.now(),

    var endDateTime: LocalDateTime = LocalDateTime.now()
)
