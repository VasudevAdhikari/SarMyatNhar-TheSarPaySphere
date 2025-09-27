package com.application.sarmyatnhar.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.application.sarmyatnhar.R
import com.application.sarmyatnhar.data.AppDatabase
import com.application.sarmyatnhar.data.entity.AuthorProfile
import com.application.sarmyatnhar.data.entity.PaymentMethod
import com.application.sarmyatnhar.data.entity.User
import com.application.sarmyatnhar.data.entity.Wallet
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var profileCameraIcon: ImageView
    private lateinit var profileImageView: ShapeableImageView
    private lateinit var username: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var phone: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    private lateinit var day: TextInputEditText
    private lateinit var month: TextInputEditText
    private lateinit var year: TextInputEditText
    private lateinit var signUp: MaterialButton
    private lateinit var closeBtn: MaterialButton

    private var selectedProfileUri: Uri? = null
    private var selectedProfileBitmap: Bitmap? = null

    // Camera launcher
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                profileImageView.setImageBitmap(bitmap)
                selectedProfileBitmap = bitmap
                selectedProfileUri = null // reset URI, since bitmap is used
            }
        }

    // Gallery launcher
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                profileImageView.setImageURI(uri)
                selectedProfileUri = uri
                selectedProfileBitmap = null
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        closeBtn = findViewById<MaterialButton>(R.id.close_button_register)
        closeBtn.setOnClickListener {
            this.finish()
        }

        // Switch to Login screen
        val goToLoginButton: TextView = findViewById(R.id.go_to_login_button)
        goToLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Bind views
        profileCameraIcon = findViewById(R.id.profile_icon)
        profileImageView = findViewById(R.id.profile_image)
        username = findViewById(R.id.activity_register_username)
        email = findViewById(R.id.activity_register_email)
        phone = findViewById(R.id.activity_register_phone)
        password = findViewById(R.id.activity_register_password)
        confirmPassword = findViewById(R.id.activity_register_confirm_password)
        day = findViewById(R.id.activity_register_day)
        month = findViewById(R.id.activity_register_month)
        year = findViewById(R.id.activity_register_year)
        signUp = findViewById(R.id.signup_button)

        // Camera button click
        profileCameraIcon.setOnClickListener { showImagePickerDialog() }

        // SignUp click
        signUp.setOnClickListener {
            val usernameStr = username.text.toString().trim()
            val emailStr = email.text.toString().trim()
            val phoneStr = phone.text.toString().trim()
            val passwordStr = password.text.toString().trim()
            val confirmPasswordStr = confirmPassword.text.toString().trim()

            // Validation
            if (usernameStr.isEmpty() || emailStr.isEmpty() || passwordStr.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (passwordStr != confirmPasswordStr) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save profile image to internal storage if selected
            var profileImagePath: String? = null
            if (selectedProfileBitmap != null) {
                val file = File(filesDir, "profile_${System.currentTimeMillis()}.jpg")
                FileOutputStream(file).use { out ->
                    selectedProfileBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                profileImagePath = file.absolutePath
            } else if (selectedProfileUri != null) {
                val inputStream = contentResolver.openInputStream(selectedProfileUri!!)
                val file = File(filesDir, "profile_${System.currentTimeMillis()}.jpg")
                inputStream?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                profileImagePath = file.absolutePath
            }

            // Create User
            val newUser = User(
                username = usernameStr,
                email = emailStr,
                password = passwordStr,
                role = "user",
                phone = phoneStr.ifEmpty { null },
                profile_url = profileImagePath // save file path if image selected
            )

            val authorProfile = AuthorProfile(user_id = 0, pen_name = "", bio = "")
            val wallet = Wallet(income_points = 0, read_points = 0, user_id = 0)
            val paymentMethod = PaymentMethod(user_id = 0, method_type = "", account_num = "", account_name = "")

            // Save to Room DB
            val db = AppDatabase.getDatabase(this)
            lifecycleScope.launch {
                val userId = db.userDao().insert(newUser).toInt()
                authorProfile.user_id = userId
                wallet.user_id = userId
                paymentMethod.user_id = userId
                db.authorProfileDao().insert(authorProfile)
                db.walletDao().insert(wallet)
                db.paymentMethodDao().insert(paymentMethod)

                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "User registered successfully!", Toast.LENGTH_SHORT).show()
                    goToLoginButton.performClick()
                }
            }
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(this)
            .setTitle("Select Profile Picture")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePictureLauncher.launch(null)
                    1 -> pickImageLauncher.launch("image/*")
                }
            }
            .show()
    }
}
