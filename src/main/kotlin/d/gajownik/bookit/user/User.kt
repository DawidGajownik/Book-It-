package d.gajownik.bookit.user

import d.gajownik.bookit.company.Company
import d.gajownik.bookit.service.Service
import jakarta.persistence.*

@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var username: String = "",
    var email: String = "",
    var password: String = "",
    var roles: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    var company: Company? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "service_users",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "service_id")]
    )
    var services: MutableList<Service> = mutableListOf(), // Wiele użytkowników może być przypisanych do jednej usługi
)
