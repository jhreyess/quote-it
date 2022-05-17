package com.example.quoteit.ui.signin

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.quoteit.R
import com.example.quoteit.data.PreferencesDataStore
import com.example.quoteit.data.dataStore
import com.example.quoteit.databinding.FragmentRegisterBinding
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
        userPreferences = PreferencesDataStore(requireContext().dataStore)
        // Bindings
        binding.loadingScreen.setOnClickListener { /* This will prevent clicks from behind views */ }
        binding.loginLink.setOnClickListener { goToLogin() }
        binding.registerButton.setOnClickListener {
            hideSoftKeyboard(view)
            binding.errorView.text = ""
            val email = binding.userEmail.text.toString()
            val username = binding.userName.text.toString()
            val password = binding.userPassword.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            presenter.register(email, username, password, confirmPassword)
        }
    }

    override fun goToLogin() { findNavController().popBackStack() }
    override fun showEmptyFieldsError() { binding.errorView.text = resources.getString(R.string.error_empty_fields) }
    override fun showInvalidPasswordsError() { binding.errorView.text = resources.getString(R.string.error_invalid_passwords) }
    override fun showWeakPasswordError() { binding.errorView.text = resources.getString(R.string.error_weak_password) }
    override fun showLoadingScreen(loading: Boolean) {
        binding.loadingScreen.visibility = if(loading) View.VISIBLE else View.GONE
    }
    override fun showExceptionError(exception: Exception) {
        Toast.makeText(activity?.applicationContext,
            exception.message, Toast.LENGTH_LONG).show()
    }
    override fun showWrongCredentialsError(error: String?) { binding.errorView.text = error ?: "" }
    override fun launchApp(token: String?, username: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val email = binding.userEmail.text.toString()
            val password = binding.userPassword.text.toString()
            userPreferences.saveLogInCredentials(email, password, requireContext())
            userPreferences.saveUsernamePreference(username, requireContext())
            token?.let { userPreferences.saveToken(it ,requireContext()) }
            userPreferences.saveLogInPreference(true, requireContext())
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