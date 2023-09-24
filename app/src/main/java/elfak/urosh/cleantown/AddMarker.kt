package elfak.urosh.cleantown

import android.Manifest
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "latitude"
private const val ARG_PARAM2 = "longitude"

/**
 * A simple [Fragment] subclass.
 * Use the [AddMarker.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddMarker : Fragment() {
    // TODO: Rename and change types of parameters
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var image: Uri
    private val IMAGE_PICK_REQUEST_CODE = 666
    private val locationViewModel: LocationViewModel by activityViewModels()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance("gs://cleantown-4d5da.appspot.com")
    private val db =
        FirebaseDatabase.getInstance("https://cleantown-4d5da-default-rtdb.europe-west1.firebasedatabase.app/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latitude = it.getDouble(ARG_PARAM1)
            longitude = it.getDouble(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_marker, container, false)
        val navController = findNavController(requireActivity(), R.id.nav_host_fragment)
        lateinit var markerType: MarkerType
        val location = locationViewModel.getLocation()
        val editTitle = view.findViewById<EditText>(R.id.editTitle)
        val editDesc = view.findViewById<EditText>(R.id.editDesc)
        val spinner = view.findViewById<Spinner>(R.id.spinner)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        btnSubmit.setOnClickListener {
            when (spinner.selectedItem.toString()) {
                "Event" -> markerType = MarkerType.Event
                else -> markerType = MarkerType.Other
            }
            val markerId = UUID.randomUUID().toString()
            storage.reference.child("markers").child(markerId).putFile(image).addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {
                    val marker = HashMap<String, Any>()
                    marker["id"] = markerId
                    marker["userId"] = auth.currentUser!!.uid;
                    marker["latitude"] = location.latitude;
                    marker["longitude"] = location.longitude;
                    marker["title"] = editTitle.text.toString();
                    marker["description"] = editDesc.text.toString();
                    marker["type"] = markerType.toString()
                    marker["photo"] = it.toString()
                    db.reference.child("markers").child(markerId).setValue(marker)
                    db.reference.child("users").child(auth.currentUser!!.uid).get().addOnSuccessListener {
                        val user = it.getValue(User::class.java)!!
                        when ( markerType ) {
                            MarkerType.Event -> {
                                val data = HashMap<String, Any>()
                                data["eventPoints"] = user.eventPoints + 5;
                                db.reference.child("users").child(auth.currentUser!!.uid).updateChildren(data)
                            }
                            MarkerType.Other -> {
                                val data = HashMap<String, Any>()
                                data["postedPoints"] = user.postedPoints + 1;
                                db.reference.child("users").child(auth.currentUser!!.uid).updateChildren(data)
                            }
                        }
                    }
                }
                navController.navigate(R.id.action_addMarker_to_mapsFragment)
            }
        }
        val btnPhoto = view.findViewById<Button>(R.id.btnImage)
        btnPhoto.setOnClickListener {
            val permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            ActivityCompat.requestPermissions(requireActivity(), permissions, 666)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoFile: File? = createImageFile()

            if (photoFile != null) {
                image = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().packageName + ".provider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, image)
                startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
            }
        }
        return view
    }

    private fun createImageFile(): File? {
        // Create a unique image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFileName = "JPEG_${timeStamp}_"
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddMarker.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddMarker().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}