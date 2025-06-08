package com.talhaatif.tickojet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.talhaatif.tickojet.local.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle splash screen for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val splashScreen = installSplashScreen()
            splashScreen.setKeepOnScreenCondition { true }
        } else {
            // Traditional splash screen for older versions
            setContentView(R.layout.activity_splash)
            supportActionBar?.hide()
        }

        lifecycleScope.launch {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                delay(1000) // Only needed for traditional splash
            }

            checkAuthAndNavigate()
        }
    }

    private suspend fun checkAuthAndNavigate() {
        val tokenManager = TokenManager(this)
        val token = withContext(Dispatchers.IO) {
            tokenManager.getToken().firstOrNull()
        }

        val intent = if (!token.isNullOrEmpty()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}