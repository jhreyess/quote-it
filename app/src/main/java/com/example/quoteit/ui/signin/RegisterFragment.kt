package com.example.quoteit.ui.signin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.quoteit.R
import com.example.quoteit.databinding.FragmentRegisterBinding
import com.example.quoteit.ui.MainActivity

class RegisterFragment : Fragment(), RegisterContract.View {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        // Listeners
        binding.loginLink.setOnClickListener { goToLogin() }
        binding.registerButton.setOnClickListener {
            //TODO("presenter.register()")
            launchApp()
        }

        return binding.root
    }

    override fun goToLogin() { findNavController().navigate(R.id.action_registerFragment_to_signInFragment) }
    override fun showEmptyEmailError() { TODO("Not yet implemented") }
    override fun showEmptyFieldsError() { TODO("Not yet implemented") }
    override fun showEmptyPasswordError() { TODO("Not yet implemented") }
    override fun showLoadingScreen() { TODO("Not yet implemented") }
    override fun launchApp() {
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}