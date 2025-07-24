package com.example.videocall

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Removed auto-navigation to HomeActivity

        setContent {
            LoginScreen(
                onLoginSuccess = {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
            )
        }
    }
}
