package elfak.urosh.cleantown

import androidx.lifecycle.ViewModel

class FiltersViewModel : ViewModel() {
    var radiusSize: Int = 100000
    var useRadius: Boolean = false
    var markerType: MarkerType = MarkerType.Other
    var useFilters: Boolean = false

    fun setRadius(size: Int) {
        radiusSize = size
    }

    fun setType(type: MarkerType) {
        markerType = type
    }
}