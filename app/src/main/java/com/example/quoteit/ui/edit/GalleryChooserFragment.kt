package com.example.quoteit.ui.edit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.quoteit.R
import com.example.quoteit.databinding.FragmentGalleryBinding
import com.example.quoteit.ui.QuoteItApp
import com.example.quoteit.ui.utils.LocalImages
import com.google.android.material.snackbar.Snackbar

class GalleryChooserFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val model by activityViewModels<ImageEditViewModel>{
        ImageEditViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) requestPermission()
        else binding()
    }

    private fun binding(){
        val data = LocalImages.getImages()
        val recycler = binding.galleryRecycler
        val adapter = GalleryAdapter(data, requireContext())
        adapter.setOnImageSelectedListener {
            model.addDrawable(it)
            editImage("drawable")
        }
        adapter.setOnPickImageListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.type = "image/*"
            requestImageLauncher.launch(Intent.createChooser(intent, resources.getString(R.string.select_image)))
        }
        recycler.setHasFixedSize(true)
        recycler.adapter = adapter
    }

    private val requestImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                it.data?.let { uri -> model.addUri(uri.data.toString()) }
                editImage("uri")
            }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){ isGranted ->
            if (isGranted) {
                Log.i("DEBUG", "====== Granted ======")
                binding()
            } else {
                Log.i("DEBUG", "====== Denied ======")
            }
        }

    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED  -> {
                // Permission already granted
                binding()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                // Permission was denied before
                Snackbar.make(requireView(), "Permission required", Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok") { requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE) }
                    .show()
            }

            else -> {
                // Permission hasn't been asked
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun editImage(type: String){
        val bundle = bundleOf("resourceType" to type)
        val action = R.id.action_galleryChooserFragment_to_imagePreviewFragment
        findNavController().navigate(action, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}