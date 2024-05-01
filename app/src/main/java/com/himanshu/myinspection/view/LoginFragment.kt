package com.example.inspectionapplication.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.inspectionapplication.model.roomdatabase.UserDatabase
import com.example.inspectionapplication.viewmodel.LoginViewModel
import com.example.inspectionapplication.viewmodel.LoginViewModelFactory
import com.himanshu.myinspection.R
import com.himanshu.myinspection.databinding.FragmentLoginBinding
import com.himanshu.myinspection.model.repository.UserRepository
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        val database = UserDatabase.getInstance(requireContext().applicationContext)
        val repository = UserRepository(database.userDao())
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(repository)).get(LoginViewModel::class.java)


        setupListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle back button press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun setupListener(){
        binding.btnLogin.setOnClickListener {
            val userName = binding.editTextUsername.toString()
            val password = binding.editTextPassword.toString()
            isAllFieldsChecked = checkAllFields()

            if (isAllFieldsChecked) {
//                performLogin(userName.toString(),password.toString())
                login(""+binding.edtUsername.text.toString(), ""+binding.edtPwd.text.toString())
            }
        }

        binding.textViewSignUp.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
    }

    private fun login(username: String, password: String) {
        println("Login Username = $username and Password = $password")
        loginViewModel.viewModelScope.launch {
            loginViewModel.getUser(username, password)?.let {

                println("Login userdata $it")
                showToast("Login Successful")
                findNavController().navigate(R.id.questionsFragment)

            } ?: showToast("Invalid username or password")
        }
    }

    private fun checkAllFields(): Boolean {
        if (binding.edtUsername.length() == 0) {
            binding.edtUsername.error = "Username is required"
            return false
        }
        if (binding.edtPwd.length() == 0) {
            binding.edtPwd.error = "Password is required"
            return false
        } else if (binding.edtPwd.length() < 6) {
            binding.edtPwd.error = "Password must be minimum 6 characters"
            return false
        }

        // after all validation return true.
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}