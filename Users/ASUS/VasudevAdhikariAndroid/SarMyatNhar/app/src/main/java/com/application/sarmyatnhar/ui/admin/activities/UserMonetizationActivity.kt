package com.application.sarmyatnhar.ui.activities

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.application.sarmyatnhar.ui.components.AdminBottomNavigationBar
import com.application.sarmyatnhar.ui.theme.SarMyatNharTheme
import com.application.sarmyatnhar.ui.theme.primaryColor
import com.application.sarmyatnhar.ui.theme.secondaryColor
import com.application.sarmyatnhar.ui.theme.whiteColor
import kotlinx.coroutines.launch

class UserMonetizationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SarMyatNharTheme {
                UserMonetizationScreen()
            }
        }
    }
}

data class UserMonetizationRequest(
    val id: Int,
    val userId: Int,
    val username: String,
    val phone: String,
    val profileImageUrl: String,
    val paymentAccountNumber: String,
    val paymentAccountName: String,
    val coinAmount: Int,
    val totalMoney: Double
)

// Mock data for the monetization requests
private val mockMonetizationRequests = listOf(
    UserMonetizationRequest(1, 1, "Ahmad Elmasry", "+201012345678", "https://placehold.co/200x200/6A4E45/ffffff?text=AE", "1234-5678-9012", "Ahmad Elmasry", 1500, 150.0),
    UserMonetizationRequest(2, 2, "Jane Doe", "+15551234567", "https://placehold.co/200x200/98C1D9/000000?text=JD", "9876-5432-1098", "Jane Doe", 2500, 250.0),
    UserMonetizationRequest(3, 3, "Ali Ahmed", "+971501234567", "https://placehold.co/200x200/F4D35E/000000?text=AA", "4567-8901-2345", "Ali Ahmed", 750, 75.0)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMonetizationScreen() {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    var selectedItem by remember { mutableStateOf("Monetization") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var pendingRequest by remember { mutableStateOf<UserMonetizationRequest?>(null) }
    var payoutProofUri by remember { mutableStateOf<Uri?>(null) }
    var approvalMessage by remember { mutableStateOf("") }
    var rejectionMessage by remember { mutableStateOf("") }
    var monetizationRequests by remember { mutableStateOf<List<UserMonetizationRequest>>(emptyList()) }
    var conversionRequests by remember { mutableStateOf<List<com.application.sarmyatnhar.data.entity.ConversionRequest>>(emptyList()) }
    LaunchedEffect(Unit) {
        val db = com.application.sarmyatnhar.data.AppDatabase.getDatabase(context)
        val conversionRequestDao = db.conversionRequestDao()
        val paymentMethodDao = db.paymentMethodDao()
        val userDao = db.userDao()
        conversionRequests = conversionRequestDao.getByType("income_to_money")
        val requests = mutableListOf<UserMonetizationRequest>()
        for (cr in conversionRequests) {
            val paymentMethod = cr.payment_method_id?.let { paymentMethodDao.getByUser(it) }
            val user = paymentMethod?.user_id?.let { userDao.getUserById(it) }
            if (user != null && paymentMethod != null && cr.status == "pending") {
                requests.add(
                    UserMonetizationRequest(
                        id = cr.id,
                        userId = user.id,
                        username = user.username,
                        phone = user.phone ?: "",
                        profileImageUrl = user.profile_url ?: "",
                        paymentAccountNumber = paymentMethod.account_num,
                        paymentAccountName = paymentMethod.account_name,
                        coinAmount = cr.amount_points,
                        totalMoney = cr.amount_points.toDouble() // You can convert points to money as needed
                    )
                )
            }
        }
        monetizationRequests = requests
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Admin Panel - Monetization",
                        color = whiteColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = whiteColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
            )
        },
        bottomBar = {
            AdminBottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { newRoute ->
                    selectedItem = newRoute
                    when (newRoute) {
//                        "Home" -> context.startActivity(Intent(context, HomeActivity::class.java))
//                        "Users" -> context.startActivity(Intent(context, UserListActivity::class.java))
//                        "Coin Requests" -> context.startActivity(Intent(context, CoinRequestActivity::class.java))
//                        "Profile" -> context.startActivity(Intent(context, ProfileActivity::class.java))
//                        "Monetization" -> { /* Stay on this screen */ }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F5FF))
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(monetizationRequests) { request ->
                MonetizationRequestItem(
                    request = request,
                    onSuccessfulClick = {
                        pendingRequest = it
                        showSuccessDialog = true
                    },
                    onRejectClick = {
                        pendingRequest = it
                        showRejectDialog = true
                    }
                )
            }
        }
    }

    // Successful Payout Dialog
    if (showSuccessDialog && pendingRequest != null) {
        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->
            if (bitmap != null) {
                val file = java.io.File(context.cacheDir, "payout_proof_${pendingRequest!!.id}.jpg")
                file.outputStream().use { out ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, out)
                }
                payoutProofUri = Uri.fromFile(file)
            }
        }
        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            payoutProofUri = uri
        }
        Dialog(onDismissRequest = { showSuccessDialog = false }) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Payout Details", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = primaryColor)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            onClick = { cameraLauncher.launch(null) },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                        ) {
                            Text("Camera")
                        }
                        Button(
                            onClick = { galleryLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                        ) {
                            Text("Gallery")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (payoutProofUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = payoutProofUri),
                            contentDescription = "Payout Proof",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = approvalMessage,
                        onValueChange = { approvalMessage = it },
                        label = { Text("Approval Message") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            onClick = {
                                val db = com.application.sarmyatnhar.data.AppDatabase.getDatabase(context)
                                val conversionRequestDao = db.conversionRequestDao()
                                val walletDao = db.walletDao()
                                (context as? ComponentActivity)?.lifecycleScope?.launch {
                                    val cr = conversionRequests.find { it.id == pendingRequest!!.id }
                                    if (cr != null) {
                                        val payoutProofUrl = payoutProofUri?.toString()
                                        conversionRequestDao.updateProofAndMessage(cr.id, payoutProofUrl, approvalMessage, "approved")
                                        val paymentMethodDao = db.paymentMethodDao()
                                        val paymentMethod = cr.payment_method_id?.let { paymentMethodDao.getByUser(it) }
                                        val userId = paymentMethod?.user_id
                                        if (userId != null) {
                                            val wallet = walletDao.getByUser(userId)
                                            val newIncomePoints = (wallet?.income_points ?: 0) - cr.amount_points
                                            walletDao.updateIncomePoints(userId, newIncomePoints, System.currentTimeMillis())
                                        }
                                    }
                                }
                                Toast.makeText(context, "Payout for ${pendingRequest?.username} confirmed!", Toast.LENGTH_SHORT).show()
                                showSuccessDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                        ) {
                            Text("Confirm")
                        }
                        Button(
                            onClick = { showSuccessDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                        ) {
                            Text("Cancel", color = Color.Black)
                        }
                    }
                }
            }
        }
    }

    // Rejection Dialog
    if (showRejectDialog) {
        Dialog(onDismissRequest = { showRejectDialog = false }) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Reject Payout", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = primaryColor)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = rejectionMessage,
                        onValueChange = { rejectionMessage = it },
                        label = { Text("Rejection Message") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            onClick = {
                                // TODO: Implement logic to update database
                                Toast.makeText(context, "Payout for ${pendingRequest?.username} rejected.", Toast.LENGTH_SHORT).show()
                                showRejectDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Confirm")
                        }
                        Button(
                            onClick = { showRejectDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                        ) {
                            Text("Cancel", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonetizationRequestItem(
    request: UserMonetizationRequest,
    onSuccessfulClick: (UserMonetizationRequest) -> Unit,
    onRejectClick: (UserMonetizationRequest) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = whiteColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // User and Payment Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(model = request.profileImageUrl),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(secondaryColor)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = request.username, fontWeight = FontWeight.Bold, color = primaryColor)
                    Text(text = request.phone, fontSize = 14.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Payment details
            Column {
                Text(text = "Account Name: ${request.paymentAccountName}", fontSize = 14.sp, color = primaryColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Account Number: ${request.paymentAccountNumber}", fontSize = 14.sp, color = primaryColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Coins: ${request.coinAmount}", fontSize = 14.sp, color = primaryColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Total Money: $${request.totalMoney}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { onSuccessfulClick(request) },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Successful", color = Color.White, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onRejectClick(request) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.width(90.dp)
                ) {
                    Text("Reject", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserMonetizationScreen() {
    UserMonetizationScreen()
}
