package elfak.urosh.cleantown

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MarkerViewViewModel : ViewModel() {
    var markerId = MutableLiveData<String>()
    public var liveMarkerId : LiveData<String> = markerId

    fun setMarkerId(id: String) {
        markerId.value = id
    }
}