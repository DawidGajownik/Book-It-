package d.gajownik.bookit.user

import d.gajownik.bookit.company.Company
import d.gajownik.bookit.industry.Industry
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

//    @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne(cascade = [(CascadeType.ALL)])
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    var company: Company = Company(),

)
