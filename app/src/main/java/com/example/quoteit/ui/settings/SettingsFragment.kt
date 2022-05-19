package com.example.quoteit.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceDataStore
import androidx.preference.PreferenceFragmentCompat
import com.example.quoteit.R
import com.example.quoteit.data.PreferencesDataStore
import com.example.quoteit.data.dataStore
import com.example.quoteit.ui.SignIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.root_preferences)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageButton>(R.id.back_to_home_btn).setOnClickListener { findNavController().popBackStack() }
        findPreference<Preference>("about")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_aboutUsFragment)
            true
        }
        findPreference<Preference>("password")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_changePasswordFragment)
            true
        }
        findPreference<Preference>("session")?.setOnPreferenceClickListener {
            context?.let {
                val prefs = PreferencesDataStore(it.dataStore)
                CoroutineScope(Dispatchers.IO).launch {
                    prefs.clearDataStore(requireContext())
                    withContext(Dispatchers.Main){
                        val intent = Intent(requireActivity(), SignIn::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }
            true
        }
    }

}