package com.application.sarmyatnhar.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.application.sarmyatnhar.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay 3 seconds then start LoginActivity (or MainActivity)
        Handler(Looper.getMainLooper()).postDelayed({

            val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

            val token = sharedPref.getString("user", null) // null if not found
            val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

            if (isLoggedIn && token != null) {
                val intent = Intent(this, HomeActivity::class.java) // change to MainActivity if needed
                startActivity(intent)
                finish() // close splash so user can’t go back to it
            } else {
                val intent = Intent(this, LoginActivity::class.java) // change to MainActivity if needed
                startActivity(intent)
                finish() // close splash so user can’t go back to it
            }
        }, 3000) // 3000 milliseconds = 3 seconds
    }
}
