package com.example.quoteit.ui

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.quoteit.R
import com.example.quoteit.data.PreferencesDataStore

class SignIn : AppCompatActivity() {

    private lateinit var navController: NavController

    private var isLoading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val prefs = PreferencesDataStore(this)
        prefs.preferenceFlow.asLiveData().observe(this) { value ->
            if (value) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(0, android.R.anim.fade_out)
            }else{
                isLoading = false
            }
        }

        splashScreen.setKeepOnScreenCondition { isLoading }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


    }
}