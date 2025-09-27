package com.application.sarmyatnhar.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.application.sarmyatnhar.R
import com.application.sarmyatnhar.data.AppDatabase
import com.application.sarmyatnhar.ui.activities.RegisterActivity
import com.application.sarmyatnhar.ui.admin.activities.UsersActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var loginBtn: MaterialButton
    private lateinit var closeBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.activity_login_email)
        password = findViewById(R.id.activity_login_password)
        loginBtn = findViewById(R.id.login_button)
        closeBtn = findViewById(R.id.close_button_login)

        closeBtn.setOnClickListener {
            this.finish()
        }

        val goToRegisterButton: TextView = findViewById(R.id.go_to_register_button)
        goToRegisterButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {
            val emailStr = email.text.toString().trim()
            val passwordStr = password.text.toString().trim()

            if (emailStr.isEmpty() || passwordStr.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()

            }

            if (emailStr == "admin@gmail.com" && passwordStr == "admin123") {
                Toast.makeText(this, "Admin Logged In successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, UsersActivity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener
            }

            val db = AppDatabase.getDatabase(this)
            lifecycleScope.launch {
                val user = db.userDao().login(emailStr, passwordStr)
                runOnUiThread {
                    if (user != null) {
                        Toast.makeText(
                            this@LoginActivity,
                            "User  Logged In successfully! " + user.profile_url,
                            Toast.LENGTH_SHORT
                        ).show()

                        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

                        with(sharedPref.edit()) {
                            putString("user", user.email)   // your login token
                            putBoolean("isLoggedIn", true) // session flag
                            apply() // apply() is async, commit() is sync
                        }

                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Wrong Credentials. Try Again!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
