package com.example.quoteit.ui.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quoteit.R
import com.example.quoteit.data.network.DatabaseApi
import com.example.quoteit.data.network.UserLoginRequest
import com.example.quoteit.data.network.UserRequest
import com.example.quoteit.databinding.FragmentSignInBinding
import com.example.quoteit.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

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
      lifecycleScope.launch {
         try {
            val data = withContext(Dispatchers.IO) {
               DatabaseApi.retrofitService.queryUser(UserLoginRequest("admin@example.com", "rootroot"))
            }
            Log.d("DATA", data.toString())
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