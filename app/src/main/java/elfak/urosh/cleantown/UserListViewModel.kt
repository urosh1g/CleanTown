package elfak.urosh.cleantown

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class UserListViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance("gs://cleantown-4d5da.appspot.com")
    private val db =
        FirebaseDatabase.getInstance("https://cleantown-4d5da-default-rtdb.europe-west1.firebasedatabase.app/")

    private val _liveUsers = MutableLiveData<List<User>>()
    val users : LiveData<List<User>> = _liveUsers

    fun getUsers() {
        db.reference.get().addOnSuccessListener {
            if( it.exists() ) {
                val users = mutableListOf<User>()
                for (userSnapshot in it.children) {
                    val user = userSnapshot.getValue(User::class.java)!!
                    users.add(user)
                }
                users.sortedByDescending {
                    it.eventPoints + it.postedPoints
                 }
                this._liveUsers.value = users
            }
         }
    }
}