package elfak.urosh.cleantown

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import java.io.File
import java.util.*
import androidx.fragment.app.activityViewModels

class AuthFragment : Fragment() {

    companion object {
        fun newInstance() = AuthFragment()
    }

    private val viewModel: AuthViewModel by activityViewModels()
    private val IMAGE_PICK_REQUEST_CODE = 666
    private lateinit var image: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                AuthState.Success -> {
                    val options: NavOptions = NavOptions.Builder().setPopUpTo(R.id.authFragment, true).build()
                    Navigation.findNavController(view).navigate(R.id.action_fragmentAuth_to_fragmentMaps, null, options)
                }
                AuthState.Failure -> {
                    Toast.makeText(activity, "Invalid Credentials", Toast.LENGTH_LONG).show()
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val btnPhoto = view.findViewById<Button>(R.id.btnPhoto)
        val email = view.findViewById<EditText>(R.id.editTextEmail)
        val password = view.findViewById<EditText>(R.id.editTextPassword)
        val phone = view.findViewById<EditText>(R.id.editTextPhone)
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
        btnRegister.setOnClickListener {
            val email = email.text.toString() + "@cleantown.net"
            val result =
                viewModel.register(email, password.text.toString(), image, phone.text.toString())
            if (result != null) {
                Toast.makeText(activity, result.cause.toString(), Toast.LENGTH_LONG).show()
                Toast.makeText(activity, result.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
        btnLogin.setOnClickListener {
            val email = email.text.toString() + "@cleantown.net"
            viewModel.login(email, password.text.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Toast.makeText(activity, "ONACTIVIRYRESULT", Toast.LENGTH_LONG).show()
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
        }
    }

    private fun createImageFile(): File? {
        // Create a unique image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFileName = "JPEG_${timeStamp}_"
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

}