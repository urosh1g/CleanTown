package elfak.urosh.cleantown

enum class MarkerType {
    Event,
    Other
}

data class Marker(
    val id: String,
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val description: String,
    val type: MarkerType,
    val photo: String
)
