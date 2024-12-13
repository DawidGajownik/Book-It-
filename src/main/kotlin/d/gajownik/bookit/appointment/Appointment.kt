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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", referencedColumnName = "id", nullable = false)
    var service: Service, // Powiązanie z usługą

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id", nullable = false)
    var client: User, // Powiązanie z klientem (użytkownikiem)

    @Column(nullable = false)
    var startDateTime: LocalDateTime, // Data i godzina rozpoczęcia

    @Column(nullable = false)
    var endDateTime: LocalDateTime // Data i godzina zakończenia
)
