package com.example.quoteit.ui.signin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quoteit.R
import com.example.quoteit.data.PreferencesDataStore
import com.example.quoteit.data.dataStore
import com.example.quoteit.data.network.LoginResponse
import com.example.quoteit.databinding.FragmentSignInBinding
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
      userPreferences = PreferencesDataStore(requireContext().dataStore)

      // Bindings
      binding.loadingScreen.setOnClickListener { /* This will prevent clicks from behind views */ }
      binding.registerLink.setOnClickListener { goToRegister() }
      binding.loginButton.setOnClickListener {
         hideSoftKeyboard(view)
         binding.errorView.text = ""
         val email = binding.userEmail.text.toString()
         val password = binding.userPassword.text.toString()
         presenter.logIn(email, password)
      }
   }

   override fun goToRegister() { findNavController().navigate(R.id.action_signInFragment_to_registerFragment) }
   override fun showEmptyEmailError() { binding.errorView.text = resources.getString(R.string.error_empty_email) }
   override fun showEmptyFieldsError() { binding.errorView.text = resources.getString(R.string.error_empty_fields) }
   override fun showEmptyPasswordError() { binding.errorView.text = resources.getString(R.string.error_empty_password) }
   override fun showLoadingScreen(loading: Boolean) {
      binding.loadingScreen.visibility = if(loading) View.VISIBLE else View.GONE
   }
   override fun showExceptionError(exception: Exception) {
      Toast.makeText(requireActivity(),
         exception.message, Toast.LENGTH_LONG).show()
   }
   override fun showWrongCredentialsError(error: String?) {
      binding.errorView.text = error ?: ""
   }
   override fun launchApp(loginCredentials: LoginResponse) {
      CoroutineScope(Dispatchers.IO).launch {
         val email = binding.userEmail.text.toString()
         val password = binding.userPassword.text.toString()
         with(loginCredentials){
            userPreferences.saveLogInCredentials(
               email,
               password,
               username!!,
               userId!!,
               requireContext()
            )
            token?.let { userPreferences.saveToken(it, requireContext()) }
            userPreferences.saveLogInPreference(true, requireContext())
         }
      }
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }

   private fun hideSoftKeyboard(view: View){
      val inputMethodManager =
         requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
   }
}