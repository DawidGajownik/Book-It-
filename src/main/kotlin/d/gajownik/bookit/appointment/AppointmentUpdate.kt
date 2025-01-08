package d.gajownik.bookit.appointment

data class AppointmentUpdate(
    val id: Long,
    val label: String,
    val start: String,
    val year: Int,
    val month: Int,
    val day: Int
)