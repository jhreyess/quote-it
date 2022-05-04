package com.example.quoteit.ui.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quoteit.R
import com.example.quoteit.data.PreferencesDataStore
import com.example.quoteit.databinding.FragmentSignInBinding
import com.example.quoteit.ui.MainActivity
import kotlinx.coroutines.*

class SignInFragment : Fragment(), SignInContract.View {

   private var _binding: FragmentSignInBinding? = null
   private val binding get() = _binding!!

   private val presenter: SignInContract.Presenter<SignInContract.View> by lazy {
      SignInPresenter(this, requireActivity())
   }

   private lateinit var userPreferences: PreferencesDataStore

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSignInBinding.inflate(inflater, container, false)
      return binding.root
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      userPreferences = PreferencesDataStore(requireContext())

      // Bindings
      binding.registerLink.setOnClickListener { goToRegister() }
      binding.loginButton.setOnClickListener {
         val email = binding.userEmail.text.toString()
         val password = binding.userPassword.text.toString()
         presenter.logIn(email, password)
      }
   }

   override fun goToRegister() { findNavController().navigate(R.id.action_signInFragment_to_registerFragment) }
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