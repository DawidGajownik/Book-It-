package d.gajownik.bookit.company

import d.gajownik.bookit.address.Address
import d.gajownik.bookit.industry.Industry
import jakarta.persistence.*

@Entity
data class Company (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    var name: String = "",

    @Column(nullable = false, unique = true)
    var description: String = "",

    @Column(nullable = false, unique = true)
    var taxId: String = "",

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    var address: Address = Address(),

    @Column(nullable = false)
    var phoneNumber: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_id", referencedColumnName = "id")
    var industry: Industry? = null
)