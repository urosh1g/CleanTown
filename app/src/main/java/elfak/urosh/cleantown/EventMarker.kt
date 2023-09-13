package elfak.urosh.cleantown

enum class MarkerType {
    Event,
    Other
}

data class EventMarker(
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val description: String,
    val type: MarkerType
)
