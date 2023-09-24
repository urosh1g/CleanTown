package elfak.urosh.cleantown

enum class MarkerType {
    Event,
    Other
}

data class Marker(
    val id: String = "",
    val userId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val title: String = "",
    val description: String = "",
    val type: MarkerType = MarkerType.Other,
    val photo: String = ""
)
