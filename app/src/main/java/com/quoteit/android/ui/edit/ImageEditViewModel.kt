package com.quoteit.android.ui.edit

import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.quoteit.android.domain.models.Quote
import com.quoteit.android.ui.QuoteItApp
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

private const val FOLDER_NAME = "/QuoteIt"

class ImageEditViewModel(application: Application) : AndroidViewModel(application) {

    private val quoteRepo = getApplication<QuoteItApp>().quotesRepository
    private val postsRepo = getApplication<QuoteItApp>().postsRepository

    private val _quote = MutableLiveData<Quote>()
    val quote: LiveData<Quote> get() = _quote

    private val _selectedDrawable = MutableLiveData<Int>()
    val selectedDrawable get() = _selectedDrawable

    private val _selectedImage = MutableLiveData<String>()
    val selectedImage get() = _selectedImage

    private val _imageSaved = MutableLiveData<Boolean>()
    val imageSaved get() = _imageSaved

    fun getQuote(id: Long) {
        viewModelScope.launch {
            _quote.value = quoteRepo.get(id)
        }
    }

    fun getPost(id: Long) {
        viewModelScope.launch {
            val post = postsRepo.get(id)
            _quote.value = Quote(0, post.author, post.quote, false)
        }
    }

    fun addDrawable(res: Int){ _selectedDrawable.value = res }
    fun addUri(uri: String){ _selectedImage.value = uri }

    internal fun saveImage(bmp: Bitmap){
        viewModelScope.launch {
            _imageSaved.value = false
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveInGallery(bmp)
            }else{
                saveInGalleryAsLegacy(bmp)
            }
            _imageSaved.value = true
        }
    }

    private fun saveInGalleryAsLegacy(bmp: Bitmap){
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        val dir = Environment.DIRECTORY_PICTURES + FOLDER_NAME
        val path = Environment.getExternalStoragePublicDirectory(dir)
        val file = File(path, filename)
        file.parentFile?.let { if(!it.exists()) it.mkdir() }
        val out = FileOutputStream(file)
        out.use {
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.close()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveInGallery(bmp: Bitmap): Uri?{
        val contentResolver = getApplication<QuoteItApp>().contentResolver
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        val dir = Environment.DIRECTORY_PICTURES + FOLDER_NAME
        val path = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        File(path.path, dir).also { if(!it.exists()) it.mkdir() }
        var out: OutputStream?
        var imageUri: Uri?

        // File metadata
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, dir)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        contentResolver.also { resolver ->
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            out = imageUri?.let { resolver.openOutputStream(it) }
        }

        out?.use {
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, it)
            it.close()
        }

        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        imageUri?.let { contentResolver.update(it, contentValues, null, null) }
        return imageUri
    }
}