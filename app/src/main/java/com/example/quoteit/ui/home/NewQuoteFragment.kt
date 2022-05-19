package com.example.quoteit.ui.home

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quoteit.databinding.FragmentNewQuoteBinding
import com.example.quoteit.ui.QuoteItApp
import kotlin.math.abs
import kotlin.math.sqrt

class NewQuoteFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentNewQuoteBinding? = null
    private val binding get() = _binding!!

    private var folderId: Long = 0L

    private var acceleration = 0.0
    private var previousAcceleration = 0.0
    private var changeInAcceleration = 0.0

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor

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

        // Instancing accelerometer
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
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
            if(!valid) { /* TODO: Show error message */}
        }
        // TODO: Hide tip if showTipPrefs is false
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
        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}