package d.gajownik.bookit.service

import d.gajownik.bookit.company.Company
import d.gajownik.bookit.user.User
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
data class Service(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var duration: Int = 0, // Czas trwania w minutach

    @Column(nullable = false)
    var price: BigDecimal = BigDecimal.ZERO, // Cena w formacie BigDecimal

    @Column(nullable = false, length = 1000)
    var description: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: User? = null, // Wiele usług może być powiązanych z jednym użytkownikiem

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    var company: Company? = null // Wiele usług może być powiązanych z jedną firmą
)
