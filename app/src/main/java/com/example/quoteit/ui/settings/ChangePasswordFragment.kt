package com.example.quoteit.ui.settings

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.quoteit.data.PreferencesDataStore
import com.example.quoteit.data.dataStore
import com.example.quoteit.databinding.FragmentChangePasswordBinding
import com.example.quoteit.ui.QuoteItApp

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val model: SettingsViewModel by viewModels {
        SettingsViewModelFactory((requireActivity().application as QuoteItApp).usersRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bindings
        context?.let {
            val prefs = PreferencesDataStore(it.dataStore)
            prefs.preferenceFlow.asLiveData().observe(viewLifecycleOwner){ pref ->
                binding.userOldPassword.setText(pref.userPassword)
            }
        }
        binding.loadingScreen.setOnClickListener { /* This will prevent clicks from behind views */ }
        binding.backToSettingsBtn.setOnClickListener { findNavController().popBackStack() }
        binding.confirmNewPasswordBtn.setOnClickListener { updatePassword() }
        model.isLoading.observe(viewLifecycleOwner) { loading ->
            val visibility = if(loading) View.VISIBLE else View.GONE
            binding.loadingScreen.visibility = visibility
        }
        model.error.observe(viewLifecycleOwner) { msg ->
            if(msg.isNotBlank()){
                Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show()
            }
        }
        model.success.observe(viewLifecycleOwner) { done ->
            if(done){
                Toast.makeText(requireActivity(),
                    "La contraseña ha sido actualizada", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun updatePassword(){
        hideSoftKeyboard(requireView())
        val newPassword = binding.userNewPassword.text.toString()
        val confirmPassword = binding.userConfirmNewPassword.text.toString()
        model.updatePassword(newPassword, confirmPassword)
    }

    private fun hideSoftKeyboard(view: View){
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}