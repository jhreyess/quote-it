package com.example.quoteit.ui.signin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quoteit.R
import com.example.quoteit.databinding.FragmentSignInBinding
import com.example.quoteit.ui.MainActivity

class SignInFragment : Fragment(), SignInContract.View {

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