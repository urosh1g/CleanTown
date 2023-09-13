package elfak.urosh.cleantown

import android.net.Uri

data class User(
    val userId: String,
    val username: String,
    val postedPoints: Int,
    val eventPoints: Int,
    val profilePhoto: String
)
