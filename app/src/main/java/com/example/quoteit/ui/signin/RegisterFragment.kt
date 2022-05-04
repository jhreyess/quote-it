package com.example.quoteit.ui.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.quoteit.R
import com.example.quoteit.data.PreferencesDataStore
import com.example.quoteit.databinding.FragmentRegisterBinding
import com.example.quoteit.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterFragment : Fragment(), RegisterContract.View {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val presenter: RegisterContract.Presenter<RegisterContract.View> by lazy {
        RegisterPresenter(this, requireActivity())
    }

    private lateinit var userPreferences: PreferencesDataStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = PreferencesDataStore(requireContext())
        // Bindings
        binding.loginLink.setOnClickListener { goToLogin() }
        binding.registerButton.setOnClickListener {
            val email = binding.userEmail.text.toString()
            val username = binding.userEmail.text.toString()
            val password = binding.userPassword.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            presenter.register(email, username, password, confirmPassword)
        }
    }

    override fun goToLogin() { findNavController().navigate(R.id.action_registerFragment_to_signInFragment) }
    override fun showEmptyEmailError() { TODO("Not yet implemented") }
    override fun showEmptyFieldsError() { TODO("Not yet implemented") }
    override fun showEmptyPasswordError() { TODO("Not yet implemented") }
    override fun showLoadingScreen() { TODO("Not yet implemented") }
    override fun showWrongCredentialsError() { Log.d("Error", "Wrong Credentials...") }
    override fun launchApp() {
        CoroutineScope(Dispatchers.IO).launch {
            userPreferences.saveLogInPreference(true, requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}