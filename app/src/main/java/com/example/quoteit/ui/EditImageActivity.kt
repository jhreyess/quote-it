package com.example.quoteit.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.quoteit.R
import com.example.quoteit.ui.edit.ImageEditViewModel

private const val QUOTE_ID = "quote"
private const val POST_ID = "post"

class EditImageActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private val model: ImageEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_image)

        val quoteId = intent.getLongExtra(QUOTE_ID, 0L)
        val postId = intent.getLongExtra(POST_ID, 0L)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.edit_image_nav_host) as NavHostFragment
        navController = navHostFragment.navController

        if(quoteId != 0L) { model.getQuote(quoteId) }
        if(postId != 0L) { model.getPost(postId) }
    }
}