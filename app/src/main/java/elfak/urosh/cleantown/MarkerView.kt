package elfak.urosh.cleantown

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class MarkerView : Fragment() {

    companion object {
        fun newInstance() = MarkerView()
    }

    private val userViewModel: UserListViewModel by activityViewModels()
    private val markersViewModel: MarkerViewModel by activityViewModels()
    private val viewModel: MarkerViewViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_marker_view, container, false)
        val marker = markersViewModel.markers.value!!.find { marker -> marker.id == viewModel.markerId.value }!!
        val joinButton = view.findViewById<Button>(R.id.btnJoinEvent)
        val textTitle = view.findViewById<TextView>(R.id.textViewTitle)
        val textType = view.findViewById<TextView>(R.id.textViewType)
        val image = view.findViewById<ImageView>(R.id.markerImage)
        textTitle.text = "Title: " + marker.title
        textType.text = "Type: " + marker.type.toString()
        Glide.with(this).load(marker.photo).into(image)
        if (marker.type == MarkerType.Other) {
            joinButton.visibility = View.INVISIBLE
        }
        joinButton.setOnClickListener {
            userViewModel.enterEvent()
        }
        return view
    }
}