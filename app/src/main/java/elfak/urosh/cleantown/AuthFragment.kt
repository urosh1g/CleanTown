package elfak.urosh.cleantown

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class AuthFragment : Fragment() {

    companion object {
        fun newInstance() = AuthFragment()
    }

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_auth, container, false)
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                AuthState.Success -> {
                    Toast.makeText(activity, "Auth Success!", Toast.LENGTH_SHORT).show()
                    val fragmentManager  = requireActivity().supportFragmentManager
                    val transaction  = fragmentManager.beginTransaction()

                    val newFragment = MapsFragment()
                    transaction.replace(R.id.fragmentContainer, newFragment)
                    transaction.commit()

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
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val email = view.findViewById<EditText>(R.id.editTextEmail)
        val password = view.findViewById<EditText>(R.id.editTextPassword)
        val phone = view.findViewById<EditText>(R.id.editTextPhone)
        btnRegister.setOnClickListener {
            val result = viewModel.register(email.text.toString(), password.text.toString())
            if(result != null) {
                Toast.makeText(activity, result.cause.toString(), Toast.LENGTH_LONG).show()
                Toast.makeText(activity, result.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        // TODO: Use the ViewModel
    }

}