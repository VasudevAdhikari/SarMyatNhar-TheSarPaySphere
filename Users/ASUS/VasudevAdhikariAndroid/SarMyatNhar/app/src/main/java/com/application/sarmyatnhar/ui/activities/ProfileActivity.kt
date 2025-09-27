package com.application.sarmyatnhar.ui.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.application.sarmyatnhar.data.AppDatabase
import com.application.sarmyatnhar.data.entity.AuthorProfile
import com.application.sarmyatnhar.data.entity.User
import com.application.sarmyatnhar.ui.components.BottomNavigationBar
import com.application.sarmyatnhar.ui.theme.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

// Mock data for preview and initial state
data class UserProfile(
    val name: String,
    val email: String,
    val phone: String,
    val status: String,
    val accountCreated: String,
    val profileImageUrl: String
)

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileScreen()
        }
    }
}

@Composable
fun ProfileScreen() {
    var isEditing by remember { mutableStateOf(false) } // FIX: was 'var isEditing = false'
    val context = LocalContext.current
    var user by remember { mutableStateOf<User?>(null) }
    var authorProfile by remember { mutableStateOf<AuthorProfile?>(null) }
    var paymentMethod by remember { mutableStateOf<com.application.sarmyatnhar.data.entity.PaymentMethod?>(null) }
    val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val email = sharedPref.getString("user", null)
    LaunchedEffect(email) {
        if (email != null) {
            val db = AppDatabase.getDatabase(context)
            val userDao = db.userDao()
            val authorProfileDao = db.authorProfileDao()
            val paymentMethodDao = db.paymentMethodDao()
            val fetchedUser = userDao.getUserDetailsByEmail(email)
            user = fetchedUser
            fetchedUser?.let {
                authorProfile = authorProfileDao.getAuthorProfileByUserId(it.id)
                paymentMethod = paymentMethodDao.getByUser(it.id)
            }
        }
    }
    var selectedItem by remember { mutableStateOf("Profile") }
    val activity = context as? ComponentActivity

    // Mock User Data
    val userProfile = UserProfile(
        name = user?.username ?: "",
        email = user?.email ?:"",
        phone = user?.phone ?:"",
        status = user?.status ?:"",
        accountCreated = "10 Sept 2024",
        profileImageUrl = user?.profile_url ?:""
    )

    var tempPenName by remember { mutableStateOf("") }
    var tempBio by remember { mutableStateOf("") }
    var tempName by remember { mutableStateOf("") }
    var tempEmail by remember { mutableStateOf("") }
    var tempPhone by remember { mutableStateOf("") }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempAccountName by remember { mutableStateOf("") }
    var tempAccountNum by remember { mutableStateOf("") }
    var showConfirmSaveDialog by remember { mutableStateOf(false) }
    var showConfirmPictureDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            tempImageUri = it
            showConfirmPictureDialog = true
        }
    }

    val scrollState = rememberScrollState()

    LaunchedEffect(user, authorProfile, paymentMethod) {
        tempName = user?.username ?: ""
        tempEmail = user?.email ?: ""
        tempPhone = user?.phone ?: ""
        tempImageUri = if (user?.profile_url.isNullOrEmpty()) null else Uri.parse(user?.profile_url)
        tempPenName = authorProfile?.pen_name ?: ""
        tempBio = authorProfile?.bio ?: ""
        tempAccountName = paymentMethod?.account_name ?: ""
        tempAccountNum = paymentMethod?.account_num ?: ""
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { newRoute ->
                    selectedItem = newRoute
                    Toast.makeText(context, "Navigating to $newRoute", Toast.LENGTH_SHORT).show()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(brush = com.application.sarmyatnhar.ui.theme.gradientBrush)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                        .clickable {
                            activity?.finish()
                        }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "My Profile",
                    color = primaryColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Profile Picture
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(4.dp, goldenColor, CircleShape)
                    .clickable(enabled = isEditing) {
                        if (isEditing) {
                            imagePickerLauncher.launch("image/*")
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                val imagePainter = rememberAsyncImagePainter(
                    model = when {
                        tempImageUri != null -> tempImageUri
                        !user?.profile_url.isNullOrEmpty() -> {
                            val file = java.io.File(user?.profile_url!!)
                            if (file.exists()) Uri.fromFile(file) else user?.profile_url
                        }
                        !userProfile.profileImageUrl.isNullOrEmpty() -> userProfile.profileImageUrl
                        else -> null
                    }
                )
                Image(
                    painter = imagePainter,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                if (isEditing) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile Picture",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 8.dp, y = 8.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = 0.8f))
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Information
            ProfileInfoField(
                label = "Name",
                value = tempName,
                onValueChange = { if (isEditing) tempName = it },
                isEditable = isEditing // Name is editable now
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProfileInfoField(
                label = "Email",
                value = tempEmail,
                onValueChange = { if (isEditing) tempEmail = it },
                isEditable = isEditing
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProfileInfoField(
                label = "Phone",
                value = tempPhone,
                onValueChange = { if (isEditing) tempPhone = it },
                isEditable = isEditing,
                keyboardType = KeyboardType.Phone
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProfileInfoField(
                label = "Status",
                value = user?.status ?: "",
                onValueChange = {},
                isEditable = false
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProfileInfoField(
                label = "Account Created",
                value = user?.let { java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(it.created_at)) } ?: "",
                onValueChange = {},
                isEditable = false
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Author Profile Information
            ProfileInfoField(
                label = "Pen Name",
                value = tempPenName,
                onValueChange = { if (isEditing) tempPenName = it },
                isEditable = isEditing
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProfileInfoField(
                label = "Bio",
                value = tempBio,
                onValueChange = { if (isEditing) tempBio = it },
                isEditable = isEditing,
                keyboardType = KeyboardType.Text,
                isMultiLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Payment Method Information
            ProfileInfoField(
                label = "Account Name",
                value = tempAccountName,
                onValueChange = { if (isEditing) tempAccountName = it },
                isEditable = isEditing
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProfileInfoField(
                label = "Account Number",
                value = tempAccountNum,
                onValueChange = { if (isEditing) tempAccountNum = it },
                isEditable = isEditing
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            if (isEditing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = {
                            showConfirmSaveDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Confirm", color = Color.White, fontSize = 16.sp, fontFamily = FontFamily.SansSerif)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedButton(
                        onClick = {
                            isEditing = false
                            tempName = userProfile.name
                            tempEmail = userProfile.email
                            tempPhone = userProfile.phone
                            tempImageUri = if (user?.profile_url.isNullOrEmpty()) null else Uri.parse(user?.profile_url)
                            tempPenName = authorProfile?.pen_name ?: ""
                            tempBio = authorProfile?.bio ?: ""
                            tempAccountName = paymentMethod?.account_name ?: ""
                            tempAccountNum = paymentMethod?.account_num ?: ""
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor)
                    ) {
                        Text(text = "Cancel", fontSize = 16.sp, fontFamily = FontFamily.SansSerif)
                    }
                }
            } else {
                Button(
                    onClick = { isEditing = true },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Edit Profile", color = primaryColor, fontSize = 16.sp, fontFamily = FontFamily.SansSerif)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showChangePasswordDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = secondaryColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Change Password", color = blackColor, fontSize = 16.sp, fontFamily = FontFamily.SansSerif)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                OutlinedButton(
                    onClick = { showLogoutConfirmDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFFFFA500), // Orange background
                        contentColor = Color.White           // Text color
                    ),
                ) {
                    Text(text = "Logout", fontSize = 16.sp, fontFamily = FontFamily.SansSerif)
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = { showDeleteConfirmDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Red, // Orange background
                        contentColor = Color.White           // Text color
                    )
                ) {
                    Text(text = "Delete Account", fontSize = 16.sp, fontFamily = FontFamily.SansSerif)
                }
            }
        }
    }

    // Dialogs
    if (showConfirmSaveDialog) {
        ConfirmDialog(
            message = "Are you sure you want to save these changes?",
            onResult = { confirmed ->
                if (confirmed) {
                    val db = AppDatabase.getDatabase(context)
                    val userDao = db.userDao()
                    val authorProfileDao = db.authorProfileDao()
                    val paymentMethodDao = db.paymentMethodDao()
                    (context as? ComponentActivity)?.lifecycleScope?.launch {
                        user?.let {
                            val updatedUser = it.copy(
                                username = tempName,
                                email = tempEmail,
                                phone = tempPhone,
                                profile_url = tempImageUri?.toString() ?: it.profile_url
                            )
                            userDao.update(updatedUser)
                            user = updatedUser
                        }
                        authorProfile?.let {
                            val updatedAuthor = it.copy(
                                pen_name = tempPenName,
                                bio = tempBio
                            )
                            authorProfileDao.update(updatedAuthor)
                            authorProfile = updatedAuthor
                        }
                        paymentMethod?.let {
                            val updatedPayment = it.copy(
                                account_name = tempAccountName,
                                account_num = tempAccountNum
                            )
                            paymentMethodDao.update(updatedPayment)
                            paymentMethod = updatedPayment
                        }
                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                }
                isEditing = false
                showConfirmSaveDialog = false
            }
        )
    }

    if (showConfirmPictureDialog) {
        ConfirmDialog(
            message = "Do you want to change your profile picture?",
            onResult = { confirmed ->
                if (confirmed) {
                    // TODO: Logic to upload image and update URL in database
                    if (tempImageUri != null) {

                    }
                    Toast.makeText(context, "Profile picture changed!", Toast.LENGTH_SHORT).show()
                }
                showConfirmPictureDialog = false
                tempImageUri = null
            }
        )
    }

    if (showLogoutConfirmDialog) {
        ConfirmDialog(
            message = "Are you sure you want to log out?",
            onResult = { confirmed ->
                if (confirmed) {
                    // Remove user email from sharedPref and navigate to LoginActivity
                    val editor = sharedPref.edit()
                    editor.remove("user")
                    editor.apply()
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    (context as? ComponentActivity)?.finish()
                }
                showLogoutConfirmDialog = false
            }
        )
    }

    if (showDeleteConfirmDialog) {
        ConfirmDialog(
            message = "WARNING: This action is irreversible. Are you sure you want to delete your account?",
            onResult = { confirmed ->
                if (confirmed) {
                    Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show()
                }
                showDeleteConfirmDialog = false
            }
        )
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false }
        )
    }
}

@Composable
fun ProfileInfoField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEditable: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    isMultiLine: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = primaryColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = !isEditable,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, accentColor, RoundedCornerShape(12.dp)),
            singleLine = !isMultiLine,
            maxLines = if (isMultiLine) 5 else 1,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = primaryColor,
                unfocusedIndicatorColor = primaryColor,
                disabledIndicatorColor = primaryColor,
                focusedContainerColor = whiteColor,
                unfocusedContainerColor = whiteColor,
                disabledContainerColor = whiteColor
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}


@Composable
fun ChangePasswordDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var showOldPasswordDialog by remember { mutableStateOf(true) }
    var oldPasswordError by remember { mutableStateOf<String?>(null) }
    val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val email = sharedPref.getString("user", null)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showOldPasswordDialog) {
                    Text(text = "Verify Old Password", color = primaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif)
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text("Old Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = oldPasswordError != null
                    )
                    if (oldPasswordError != null) {
                        Text(text = oldPasswordError!!, color = Color.Red, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // Check old password with database
                            if (oldPassword.isNotEmpty() && email != null) {
                                val db = AppDatabase.getDatabase(context)
                                val userDao = db.userDao()
                                (context as? ComponentActivity)?.lifecycleScope?.launch {
                                    val user = userDao.getUserDetailsByEmail(email)
                                    if (user != null && user.password == oldPassword) {
                                        oldPasswordError = null
                                        showOldPasswordDialog = false
                                    } else {
                                        oldPasswordError = "Incorrect old password"
                                    }
                                }
                            } else {
                                oldPasswordError = "Please enter your old password"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Text(text = "Next", color = whiteColor, fontSize = 16.sp, fontFamily = FontFamily.SansSerif)
                    }
                } else {
                    Text(text = "Change Password", color = whiteColor, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif)
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = confirmNewPassword,
                        onValueChange = { confirmNewPassword = it },
                        label = { Text("Confirm New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (newPassword == confirmNewPassword && newPassword.isNotEmpty()) {
                                // Logic to change password in database
                                if (email != null) {
                                    val db = AppDatabase.getDatabase(context)
                                    val userDao = db.userDao()
                                    (context as? ComponentActivity)?.lifecycleScope?.launch {
                                        val user = userDao.getUserDetailsByEmail(email)
                                        if (user != null) {
                                            userDao.updatePassword(user.id, newPassword)
                                            Toast.makeText(context, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                                            onDismiss()
                                        } else {
                                            Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Passwords do not match or are empty", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Text(text = "Confirm", color = whiteColor, fontSize = 16.sp, fontFamily = FontFamily.SansSerif)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel", color = primaryColor, fontSize = 16.sp, fontFamily = FontFamily.SansSerif)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewProfileScreenScreen() {
    SarMyatNharTheme {
        ProfileScreen()
    }
}
