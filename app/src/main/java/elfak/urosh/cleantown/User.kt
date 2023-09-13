package elfak.urosh.cleantown

import android.net.Uri

data class User(
    val userId: String? = "",
    val username: String = "",
    val postedPoints: Int = 0,
    val eventPoints: Int = 0,
    val profilePhoto: String = ""
)
