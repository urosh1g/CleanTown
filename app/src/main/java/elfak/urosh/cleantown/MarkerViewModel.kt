package elfak.urosh.cleantown

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class MarkerViewModel: ViewModel() {
    private val db = FirebaseDatabase.getInstance("https://cleantown-4d5da-default-rtdb.europe-west1.firebasedatabase.app/")
    private val _liveMarkers = MutableLiveData<List<Marker>>()
    val markers : LiveData<List<Marker>> = _liveMarkers

    fun setMarkers(markers: List<Marker>) {
        _liveMarkers.value = markers
    }

    fun getMarkers() {
        db.reference.child("markers").get().addOnSuccessListener {
            if (it.exists()) {
                var markers = mutableListOf<Marker>()
                for( markerSnapshot in it.children) {
                    val marker = markerSnapshot.getValue(Marker::class.java)!!
                    markers.add(marker)
                }
                _liveMarkers.value = markers
            }
        }
    }
}