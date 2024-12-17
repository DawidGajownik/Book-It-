package d.gajownik.bookit.industry

import d.gajownik.bookit.company.Company
import jakarta.persistence.*

@Entity
data class Industry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var name: String? = null,
    var description: String? = null,

    @Lob
    var image: ByteArray? = null,

    @Column(columnDefinition = "LONGTEXT")
    var base64Image: String? = null)
{
    constructor(id: Long, name: String, description: String) : this(
        id = id,
        name = name,
        description = description,
        image = null
    )
}
