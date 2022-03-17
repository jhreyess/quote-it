package com.example.quoteit.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quoteit.R
import com.example.quoteit.databinding.FragmentSignInBinding

interface FragmentSignInView {
   fun launchApp()
   fun showLoadingScreen()
   fun showEmptyEmailError()
   fun showEmptyPasswordError()
   fun showEmptyFieldsError()
   fun showWrongCredentialsError()
   fun goToRegister()
}

class SignInFragment : Fragment(), FragmentSignInView {

   private var _binding: FragmentSignInBinding? = null
   private val binding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSignInBinding.inflate(inflater, container, false)
      val view = binding.root

      // Listeners
      binding.registerLink.setOnClickListener { goToRegister() }
      binding.loginButton.setOnClickListener {
         //TODO("presenter.signIn()")
         launchApp()
      }

      return view
   }

   override fun goToRegister() { findNavController().navigate(R.id.action_signInFragment_to_registerFragment) }
   override fun showEmptyEmailError() { TODO("Not yet implemented") }
   override fun showEmptyFieldsError() { TODO("Not yet implemented") }
   override fun showEmptyPasswordError() { TODO("Not yet implemented") }
   override fun showLoadingScreen() { TODO("Not yet implemented") }
   override fun showWrongCredentialsError() { TODO("Not yet implemented") }
   override fun launchApp() { (activity as SignIn).launchApp()  }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }
}