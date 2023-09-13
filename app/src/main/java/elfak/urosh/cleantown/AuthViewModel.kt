package elfak.urosh.cleantown

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlin.Exception

enum class AuthState {
    Success,
    Failure
}

class AuthViewModel : ViewModel() {
    private val _liveAuthState = MutableLiveData<AuthState>()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance("gs://cleantown-4d5da.appspot.com")
    private val db =
        FirebaseDatabase.getInstance("https://cleantown-4d5da-default-rtdb.europe-west1.firebasedatabase.app/")
    val authState: LiveData<AuthState> = _liveAuthState

    fun isAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _liveAuthState.value = AuthState.Success
                } else {
                    _liveAuthState.value = AuthState.Failure
                }
            }
    }

    fun register(email: String, password: String, img: Uri?, phone: String): Exception? {
        var exception: Exception? = null
        if (img == null) {
            return Exception("banana")
        }
        val storageRef = storage.reference
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser!!.uid.toString()
                    storageRef.child("profile_images").child(userId).putFile(img)
                        .addOnSuccessListener { task ->
                            task.storage.downloadUrl.addOnSuccessListener { uri ->
                                var user = HashMap<String, Any>()
                                user["username"] = email.split('@')[0]
                                user["profilePhoto"] = uri.toString()
                                user["phone"] = phone
                                user["userId"] = userId
                                user["postedPoints"] = 0
                                user["eventPoints"] = 0
                                db.reference.child(userId).setValue(user)
                                    .addOnFailureListener { ex ->
                                        Log.d(
                                            "DB",
                                            ex.message.toString()
                                        )
                                    }
                            }
                        }
                        .addOnFailureListener { ex ->
                            Log.d("STORAGE", ex.message.toString())
                        }
                    _liveAuthState.value = AuthState.Success
                } else {
                    _liveAuthState.value = AuthState.Failure
                }
            }
            .addOnFailureListener { ex ->
                exception = ex
            }
        return exception
    }
}