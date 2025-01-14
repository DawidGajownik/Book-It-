package d.gajownik.bookit.company

import d.gajownik.bookit.address.Address
import d.gajownik.bookit.industry.Industry
import d.gajownik.bookit.user.User
import jakarta.persistence.*
import java.time.LocalTime

@Entity
data class Company(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    var name: String = "",

    @Column(nullable = false, unique = true)
    var description: String = "",

    @Column(nullable = false, unique = true)
    var taxId: String = "",

    @Column(columnDefinition = "LONGTEXT")
    var base64Image: String? = null,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    var address: Address = Address(),

    @Column(nullable = false)
    var phoneNumber: String = "",

    @OneToMany(mappedBy = "company", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var users: MutableList<User> = mutableListOf(),

    @ManyToMany(fetch = FetchType.LAZY)
    var industry: MutableList<Industry> = mutableListOf(),

    var workTimeStart: LocalTime = LocalTime.of(9, 0),
    var workTimeEnd: LocalTime = LocalTime.of(17, 0),

    @ElementCollection
    @Column(nullable = false)
    var workingDays: MutableList<Int> = mutableListOf(),
)
