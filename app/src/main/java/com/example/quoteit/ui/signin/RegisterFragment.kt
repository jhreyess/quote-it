package com.example.quoteit.ui.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quoteit.R
import com.example.quoteit.data.network.DatabaseApi
import com.example.quoteit.data.network.UserLoginRequest
import com.example.quoteit.data.network.UserRequest
import com.example.quoteit.databinding.FragmentRegisterBinding
import com.example.quoteit.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

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
        lifecycleScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    DatabaseApi.retrofitService.insertUser(UserRequest("admin","admin@example.com", "rootroot"))
                }
                if(data.success){
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}