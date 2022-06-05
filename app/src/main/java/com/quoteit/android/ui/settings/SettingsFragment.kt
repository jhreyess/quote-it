package com.quoteit.android.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.android.material.appbar.MaterialToolbar
import com.quoteit.android.R
import com.quoteit.android.data.PreferencesDataStore
import com.quoteit.android.data.dataStore
import com.quoteit.android.ui.QuoteItApp
import com.quoteit.android.ui.SignIn
import com.quoteit.android.ui.utils.ConfirmDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : PreferenceFragmentCompat() {

    val model: DeleteAccountViewModel by viewModels {
        DeleteAccountViewModelFactory((requireActivity().application as QuoteItApp).usersRepository)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.root_preferences)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialToolbar>(R.id.settings_toolbar).apply {
            setupWithNavController(findNavController())
            setNavigationIcon(R.drawable.ic_arrow_back)
        }
        findPreference<Preference>("about")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_aboutUsFragment)
            true
        }
        findPreference<Preference>("password")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_changePasswordFragment)
            true
        }
        findPreference<Preference>("deleteAccount")?.setOnPreferenceClickListener {
            showAlert(resources.getString(R.string.confirm_delete_account)){
                model.deleteAccount()
            }
            true
        }
        findPreference<Preference>("session")?.setOnPreferenceClickListener {
            closeSession()
            true
        }
        val prefs = PreferencesDataStore(requireContext().dataStore)
        val vibrateSwitch = findPreference<SwitchPreference>("vibrationPref")
        prefs.preferenceVibration.asLiveData().observe(viewLifecycleOwner){
            vibrateSwitch?.isChecked = it
        }
        vibrateSwitch?.setOnPreferenceChangeListener { _, newValue ->
            CoroutineScope(Dispatchers.IO).launch {
                prefs.saveVibrationPref(newValue as Boolean, requireContext())
            }
            true
        }

        // Model
        model.success.observe(viewLifecycleOwner){ success -> if(success) closeSession() }
        model.isLoading.observe(viewLifecycleOwner){ visible ->
            val visibility = if(visible) View.VISIBLE else View.GONE
            view.findViewById<FrameLayout>(R.id.loadingScreen).visibility = visibility
        }
        model.error.observe(viewLifecycleOwner){ err ->
            if(err.isNotBlank()){
                Toast.makeText(requireActivity(), err,
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun closeSession(){
        context?.let {
            val prefs = PreferencesDataStore(it.dataStore)
            CoroutineScope(Dispatchers.IO).launch {
                prefs.clearDataStore(requireContext())
                (requireActivity().application as QuoteItApp).clearDatabase()
                withContext(Dispatchers.Main){
                    val intent = Intent(requireActivity(), SignIn::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }
    }

    private fun showAlert(message: String, callback: () -> Unit){
        val dialog = ConfirmDialog(message)
        dialog.setOnConfirmListener(callback)
        dialog.show(parentFragmentManager, "confirm_delete_dialog")
    }

}