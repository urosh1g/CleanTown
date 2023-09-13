package elfak.urosh.cleantown

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserProfile : Fragment() {
    private val authViewModel: AuthViewModel by activityViewModels()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance("https://cleantown-4d5da-default-rtdb.europe-west1.firebasedatabase.app/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)
        val userId = auth.currentUser!!.uid
        val user = db.reference.child(userId).get().addOnSuccessListener {
            if (it.exists()) {
                val image = view.findViewById<ImageView>(R.id.imageView)
                val username = view.findViewById<TextView>(R.id.textViewUsername)
                val pointsEvents = view.findViewById<TextView>(R.id.textViewPointsEvents)
                val pointsCreated = view.findViewById<TextView>(R.id.textViewPointsCreated)
                val totalPoints = view.findViewById<TextView>(R.id.totalPoints)
                val user = it.getValue(User::class.java)!!
                Glide.with(this).load(user.profilePhoto).into(image)
                username.text = "Username: " + user.username
                pointsEvents.text = "Event points: " + user.eventPoints.toString()
                pointsCreated.text = "Posted points: " + user.postedPoints.toString()
                totalPoints.text = "Total points: " + (user.eventPoints + user.postedPoints).toString()
            }
         }
        return view
    }
}