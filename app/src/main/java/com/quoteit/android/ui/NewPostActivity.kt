package com.quoteit.android.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.quoteit.android.R

class NewPostActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.new_post_nav_host) as NavHostFragment
        navController = navHostFragment.navController
    }
}