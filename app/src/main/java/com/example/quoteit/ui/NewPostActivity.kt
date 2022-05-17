package com.example.quoteit.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.quoteit.R

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