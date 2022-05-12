package com.example.quoteit.ui.edit

import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.work.WorkManager
import com.example.quoteit.domain.models.Quote
import com.example.quoteit.ui.QuoteItApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ImageEditViewModel(
    private val application: Application
    ) : ViewModel() {

    private val quoteRepo = (application as QuoteItApp).quotesRepository

    private val _quote = MutableLiveData<Quote>()
    val quote: LiveData<Quote> get() = _quote

    private val _selectedDrawable = MutableLiveData<Int>()
    val selectedDrawable get() = _selectedDrawable

    private val _selectedImage = MutableLiveData<String>()
    val selectedImage get() = _selectedImage

    fun getQuote(id: Long) {
        viewModelScope.launch {
            _quote.value = quoteRepo.get(id)
        }
    }

    fun addDrawable(res: Int){ _selectedDrawable.value = res }
    fun addUri(uri: String){ _selectedImage.value = uri }

    internal fun saveImage(bmp: Bitmap){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveInGallery(bmp)
                }else{
                    saveInGalleryAsLegacy(bmp)
                }
            }
        }
    }

    private fun saveInGalleryAsLegacy(bmp: Bitmap){
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(dir, filename)
        val out = FileOutputStream(file)
        out.use { bmp.compress(Bitmap.CompressFormat.JPEG, 100, it)}
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveInGallery(bmp: Bitmap): Uri?{
        val contentResolver = application.applicationContext.contentResolver
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream?
        var imageUri: Uri?

        // File metadata
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        contentResolver.also { resolver ->
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }

        fos?.use { bmp.compress(Bitmap.CompressFormat.JPEG, 70, it) }

        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        imageUri?.let { contentResolver.update(it, contentValues, null, null) }

        return imageUri
    }
}

class ImageEditViewModelFactory(
    private val app: Application,
    ) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImageEditViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}