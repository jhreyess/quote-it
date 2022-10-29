package com.quoteit.android.ui.signin

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.quoteit.android.R
import com.quoteit.android.data.PreferencesDataStore
import com.quoteit.android.data.dataStore
import com.quoteit.android.data.network.LoginResponse
import com.quoteit.android.data.network.config.SessionManager
import com.quoteit.android.databinding.FragmentSignInBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
      binding.agreement.movementMethod = LinkMovementMethod.getInstance()
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
               username!!,
               password,
               userId!!,
               requireContext()
            )
            token?.let { SessionManager.setAccessToken(it) }
            refreshToken?.let { userPreferences.saveToken(it, requireContext()) }
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