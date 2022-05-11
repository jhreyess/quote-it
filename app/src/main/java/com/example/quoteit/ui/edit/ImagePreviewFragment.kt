package com.example.quoteit.ui.edit

import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.TypedValue
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.quoteit.R
import com.example.quoteit.databinding.FragmentImagePreviewBinding
import com.example.quoteit.ui.MainActivity
import com.example.quoteit.ui.QuoteItApp
import com.example.quoteit.ui.utils.ConfirmDialog
import com.google.android.material.slider.Slider
import java.io.OutputStream

class ImagePreviewFragment : Fragment() {

    companion object{
        private const val DATA_TYPE = "resourceType"
        private const val IMAGE = "uri"
        private const val DRAWABLE = "drawable"
    }

    private var _binding: FragmentImagePreviewBinding? = null
    private val binding get() = _binding!!

    private var dataType: String = ""

    private var isTextWhite: Boolean = true

    private val model: ImageEditViewModel by activityViewModels {
        ImageEditViewModelFactory((requireActivity().application as QuoteItApp).quotesRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dataType = it.getString(DATA_TYPE).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImagePreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(dataType == IMAGE){
            model.selectedImage.observe(viewLifecycleOwner) {
                binding.imagePreview.setImageURI(Uri.parse(it))
            }
        }else if(dataType == DRAWABLE){
            model.selectedDrawable.observe(viewLifecycleOwner) {
                binding.imagePreview.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), it)
                )
            }
        }

        // Bindings
        binding.confirmImageEdit.setOnClickListener { saveImage() }
        binding.cancelImageEdit.setOnClickListener {
            showAlert(resources.getString(R.string.confirm_cancel)){
                val intent = Intent(requireActivity(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.slide_in_left, android.R.anim.fade_out)
        }}
        binding.imageTextSlider.addOnChangeListener(handleSlider)
        binding.imageTextAlign.setOnClickListener { handleTextAlign() }
        binding.imageTextBold.setOnClickListener { handleTextBold() }
        binding.imageTextItalic.setOnClickListener { handleTextItalic() }
        binding.imageTextColor.setOnClickListener { handleTextColor() }
        model.quote.observe(viewLifecycleOwner) {
            val text = StringBuilder(it.quote)
                .appendLine()
                .appendLine()
                .append("- "+it.author)
            binding.imageText.text = text
        }
    }

    private val handleSlider = Slider.OnChangeListener { _, value, _ ->
        val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            value, resources.displayMetrics).toInt()
        binding.imageText.updateLayoutParams { this.height = height }
    }

    private fun handleTextAlign(){
        val gravity = when(binding.imageText.gravity){
            Gravity.CENTER_VERTICAL or Gravity.CENTER -> Gravity.END
            Gravity.CENTER_VERTICAL or Gravity.END -> Gravity.START
            Gravity.CENTER_VERTICAL or Gravity.START -> Gravity.CENTER
            else -> Gravity.START
        }

        val resource = when(gravity){
            Gravity.CENTER -> R.drawable.ic_align_center
            Gravity.END -> R.drawable.ic_align_right
            Gravity.START -> R.drawable.ic_align_left
            else -> R.drawable.ic_align_right
        }
        binding.imageTextAlign.setImageResource(resource)
        binding.imageText.gravity = Gravity.CENTER_VERTICAL or gravity
    }

    private fun handleTextBold(){
        val oldTypeface = binding.imageText.typeface
        val newStyle = when(oldTypeface.style){
            Typeface.NORMAL -> { Typeface.BOLD }
            Typeface.BOLD -> { Typeface.NORMAL }
            Typeface.ITALIC -> { Typeface.BOLD_ITALIC }
            Typeface.BOLD_ITALIC -> { Typeface.ITALIC }
            else -> Typeface.NORMAL
        }
        binding.imageText.setTypeface(Typeface.DEFAULT, newStyle)
    }

    private fun handleTextItalic(){
        val oldTypeface = binding.imageText.typeface
        val newStyle = when(oldTypeface.style){
            Typeface.NORMAL -> Typeface.ITALIC
            Typeface.BOLD -> Typeface.BOLD_ITALIC
            Typeface.ITALIC -> Typeface.NORMAL
            Typeface.BOLD_ITALIC -> Typeface.BOLD
            else -> Typeface.NORMAL
        }
        binding.imageText.setTypeface(Typeface.DEFAULT, newStyle)
    }

    private fun handleTextColor(){
        val newColor = if(isTextWhite) R.color.black else R.color.white
        isTextWhite = !isTextWhite
        binding.imageText.setTextColor(ContextCompat.getColor(requireContext(), newColor))
    }

    private fun showAlert(message: String, callback: () -> Unit){
        val dialog = ConfirmDialog(message)
        dialog.setOnConfirmListener(callback)
        dialog.show(parentFragmentManager, "confirm_delete_dialog")
    }

    private fun saveImage() {
        val bmp = createAndMergeBitmap()
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        createFile(bmp, filename)
    }

    private fun createFile(bmp: Bitmap, filename: String){

        var imageUri: Uri?
        var out: OutputStream?

        val contentResolver = requireActivity().applicationContext.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }else{
                put(Images.Media.DATE_TAKEN, System.currentTimeMillis())
            }
        }

        contentResolver.also { resolver ->
            imageUri = resolver.insert(Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            out = imageUri?.let { resolver.openOutputStream(it) }
        }

        out?.use {
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, it )
            it.close()
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        }
        imageUri?.let { contentResolver.update(it, contentValues, null, null) }
    }

    private fun createAndMergeBitmap(): Bitmap{
        val background = getBitmapFromView(binding.imagePreview)
        val foreground = getBitmapFromView(binding.imageText)
        val bmp = Bitmap.createBitmap(background.width, background.height, background.config)
        val canvas = Canvas(bmp)
        val heightBack = background.height
        val heightFront = foreground.height
        val move = ((heightBack - heightFront) / 2).toFloat()
        canvas.drawBitmap(background, 0f, 0f, null)
        canvas.drawBitmap(foreground, 0f, move, null)
        return bmp
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}