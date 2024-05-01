package com.example.inspectionapplication.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.inspectionapplication.model.roomdatabase.User
import com.example.inspectionapplication.model.roomdatabase.UserDatabase
import com.example.inspectionapplication.viewmodel.LoginViewModel
import com.example.inspectionapplication.viewmodel.LoginViewModelFactory
import com.himanshu.myinspection.R
import com.himanshu.myinspection.databinding.FragmentRegisterBinding
import com.himanshu.myinspection.model.repository.UserRepository
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RegisterFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentRegisterBinding? = null
    private lateinit var viewModel: LoginViewModel
    private val binding get() = _binding!!
    var isAllFieldsChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val database = UserDatabase.getInstance(requireContext().applicationContext)
        val repository = UserRepository(database.userDao())
        viewModel = ViewModelProvider(this, LoginViewModelFactory(repository)).get(LoginViewModel::class.java)


        setupListener()
        return binding.root
    }

    private fun setupListener(){
        val username = binding.editTextUsername.text.toString()
        val password = binding.editTextPwd.text.toString()

        // Check if username and password are not empty
        binding.btnRegister.setOnClickListener {
            isAllFieldsChecked = checkAllFields()

            if (isAllFieldsChecked) {
                register(""+binding.editTextUsername.text.toString(), ""+binding.editTextConfirmPwd.text.toString())
            }
        }
    }

    private fun register(username: String, password: String) {
        // Check if the user already exists
        viewModel.viewModelScope.launch {
            println("username = $username and password = $password")

            viewModel.getUser(username, password)?.let {
                // User already exists
                showToast("User already exists")
                println("userdata $it")
            } ?: run {
                // User doesn't exist, proceed with registration
                val newUser = User(username, password)
                viewModel.insertUser(newUser)
                showToast("Registration Successful")
                findNavController().navigate(R.id.loginFragment)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun checkAllFields(): Boolean {
        if (binding.editTextUsername.length() == 0) {
            binding.editTextUsername.error = "Username is required"
            return false
        }
        if (binding.editTextPwd.length() == 0) {
            binding.editTextPwd.error = "Password is required"
            return false
        } else if (binding.editTextPwd.length() < 6) {
            binding.editTextPwd.error = "Password must be minimum 6 characters"
            return false
        }
        if (binding.editTextConfirmPwd.length() == 0) {
            binding.editTextConfirmPwd.error = "Confirm Password is required"
            return false
        } else if (binding.editTextConfirmPwd.length() < 6) {
            binding.editTextConfirmPwd.error = "Confirm Password must be minimum 6 characters"
            return false
        }else if (binding.editTextConfirmPwd.text.toString() != binding.editTextPwd.text.toString()) {
            binding.editTextConfirmPwd.error = "Confirm Password doesn't match Password"
            return false
        }
        // after all validation return true.
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle back button press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}