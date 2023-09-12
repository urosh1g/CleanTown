package elfak.urosh.cleantown

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlin.Exception

enum class AuthState {
    Success,
    Failure
}

class AuthViewModel : ViewModel() {
    private val _liveAuthState = MutableLiveData<AuthState>()
    private val auth = FirebaseAuth.getInstance()
    val authState: LiveData<AuthState> = _liveAuthState

    fun isAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _liveAuthState.value = AuthState.Success
                }
                else {
                    _liveAuthState.value = AuthState.Failure
                }
            }
    }

    fun register(email: String, password: String): Exception? {
        var exception: Exception? = null
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _liveAuthState.value = AuthState.Success
                }
                else {
                    _liveAuthState.value = AuthState.Failure
                }
            }
            .addOnFailureListener { ex ->
                exception = ex
             }
             return exception
    }
}