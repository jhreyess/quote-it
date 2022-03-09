package com.example.quoteit.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.quoteit.R
import com.example.quoteit.databinding.FragmentRegisterBinding
import com.example.quoteit.databinding.FragmentSignInBinding

interface RegisterView {
    fun launchApp()
    fun showLoadingScreen()
    fun showEmptyEmailError()
    fun showEmptyPasswordError()
    fun showEmptyFieldsError()
    fun showWrongCredentialsError()
    fun goToLogin()
}

class RegisterFragment : Fragment(), RegisterView {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        // Listeners
        binding.loginLink.setOnClickListener { goToLogin() }
        binding.registerButton.setOnClickListener { TODO("presenter.register()") }

        return view
    }

    override fun goToLogin() { findNavController().navigate(R.id.action_registerFragment_to_signInFragment) }
    override fun showEmptyEmailError() { TODO("Not yet implemented") }
    override fun showEmptyFieldsError() { TODO("Not yet implemented") }
    override fun showEmptyPasswordError() { TODO("Not yet implemented") }
    override fun showLoadingScreen() { TODO("Not yet implemented") }
    override fun showWrongCredentialsError() { TODO("Not yet implemented") }
    override fun launchApp() { TODO("Not yet implemented")  }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}