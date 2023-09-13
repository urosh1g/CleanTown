package elfak.urosh.cleantown

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class LocationViewModel: ViewModel() {
    private val _liveLocation = MutableLiveData<LatLng>()
    val location : LiveData<LatLng> = _liveLocation

    fun setLocation(location: LatLng) {
        _liveLocation.value = location
    }

    fun getLocation(): LatLng {
        return location.value!!
    }

}