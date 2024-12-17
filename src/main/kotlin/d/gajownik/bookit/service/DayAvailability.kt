package d.gajownik.bookit.service

import lombok.Getter
import java.time.LocalTime

@Getter
class DayAvailability(
    val year: Int,
    val month: Int,
    val day: Int,
    val availableHours: List<LocalTime>,
    val name: String,
    val date: String,
    val monthName: String
)
