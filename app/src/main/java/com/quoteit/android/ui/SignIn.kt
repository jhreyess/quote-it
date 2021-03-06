package com.quoteit.android.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.work.*
import com.quoteit.android.R
import com.quoteit.android.data.PreferencesDataStore
import com.quoteit.android.data.dataStore
import com.quoteit.android.data.network.config.SessionManager
import com.quoteit.android.workers.SyncDataWorker
import java.util.concurrent.TimeUnit

private const val SYNC_DATA_WORKER = "quote-it-sync-worker"

class SignIn : AppCompatActivity() {

    private lateinit var navController: NavController

    private var isLoading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val prefs = PreferencesDataStore(baseContext.dataStore)
        prefs.preferenceToken.asLiveData().observe(this){ SessionManager.setToken(it) }
        prefs.preferenceFlow.asLiveData().observe(this) { pref ->
            if (pref.isUserLoggedIn) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(0, android.R.anim.fade_out)

                if(pref.token.isNotBlank()){

                    val syncWorkRequest = PeriodicWorkRequest.Builder(
                        SyncDataWorker::class.java,
                        30, TimeUnit.MINUTES)
                        .setConstraints(constraint)
                        .addTag(SYNC_DATA_WORKER)
                        .build()

                    WorkManager.getInstance(applicationContext)
                        .enqueueUniquePeriodicWork(
                            SYNC_DATA_WORKER,
                            ExistingPeriodicWorkPolicy.KEEP,
                            syncWorkRequest)

                }
            }else{
                isLoading = false
                WorkManager.getInstance(applicationContext)
                    .cancelAllWorkByTag(SYNC_DATA_WORKER)
            }
        }

        splashScreen.setKeepOnScreenCondition { isLoading }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


    }
}