package com.quoteit.android.ui.home

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.quoteit.android.data.PreferencesDataStore
import com.quoteit.android.data.dataStore
import com.quoteit.android.databinding.FragmentNewQuoteBinding
import com.quoteit.android.ui.QuoteItApp
import kotlin.math.abs
import kotlin.math.sqrt

class NewQuoteFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentNewQuoteBinding? = null
    private val binding get() = _binding!!

    private var folderId: Long = 0L

    private var acceleration = 0.0
    private var previousAcceleration = 0.0
    private var changeInAcceleration = 0.0

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null

    companion object{
        const val FOLDER = "folder"
    }

    private val model: NewQuoteViewModel by viewModels {
        val repos = (requireActivity().application as QuoteItApp)
        NewQuoteViewModelFactory(repos.foldersQuoteRepository, repos.quotesRepository, repos.foldersRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve folderID
        folderId = arguments?.getLong(FOLDER) ?: 0L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewQuoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bindings
        val prefs = PreferencesDataStore(requireContext().dataStore)
        binding.backToFolderBtn.setOnClickListener { findNavController().popBackStack() }
        binding.confirmQuoteBtn.setOnClickListener {
            val author = binding.quoteAuthor.editText!!.text.toString()
            val content = binding.quoteContent.editText!!.text.toString()
            model.createQuote(author, content, folderId)
        }

        model.isLoading.observe(viewLifecycleOwner) { loading ->
            if(!loading){ findNavController().popBackStack() }
        }

        model.isValidInput.observe(viewLifecycleOwner) { valid ->
            binding.errorView.visibility = if(valid) View.GONE else View.VISIBLE
        }

        prefs.preferenceVibration.asLiveData().observe(viewLifecycleOwner) { allowed ->
            binding.tipView.visibility = if(allowed) View.VISIBLE else View.GONE
            if(allowed){
                // Instancing accelerometer
                sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
                accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            }
        }
    }

    override fun onSensorChanged(ev: SensorEvent) {
        val (x,y,z) = ev.values
        acceleration = sqrt((x*x + y*y + z*z).toDouble())
        changeInAcceleration = abs(acceleration - previousAcceleration)
        previousAcceleration = acceleration

        @Suppress("DEPRECATION")
        if(changeInAcceleration > 14){
            binding.quoteAuthor.editText?.setText("")
            binding.quoteContent.editText?.setText("")
            val vibrator: Vibrator = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                val vibratorManager = activity?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            }else{
                activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            }else{
                vibrator.vibrate(500L)
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}