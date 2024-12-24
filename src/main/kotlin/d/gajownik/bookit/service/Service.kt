package d.gajownik.bookit.service

import d.gajownik.bookit.company.Company
import d.gajownik.bookit.user.User
import jakarta.persistence.*
import lombok.NoArgsConstructor
import java.math.BigDecimal
import java.time.LocalTime

@Entity
@NoArgsConstructor
data class Service(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var duration: Int = 0, // Czas trwania w minutach

    var timeSpace: Int = 0,

    @Column(nullable = false)
    var price: BigDecimal = BigDecimal.ZERO, // Cena w formacie BigDecimal

    @Column(nullable = false, length = 1000)
    var description: String = "",

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "service_users",
        joinColumns = [JoinColumn(name = "service_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    var users: MutableList<User> = mutableListOf(), // Wiele użytkowników może być przypisanych do jednej usługi

    @ElementCollection
    var startHours: MutableList<LocalTime> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    var company: Company? = null, // Wiele usług może być powiązanych z jedną firmą

    var places: Int = 0,
    var choosableEmployee: Boolean = false
)
