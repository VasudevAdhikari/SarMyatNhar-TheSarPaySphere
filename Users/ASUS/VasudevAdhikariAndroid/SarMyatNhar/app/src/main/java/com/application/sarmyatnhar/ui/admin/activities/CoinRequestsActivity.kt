package com.application.sarmyatnhar.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.application.sarmyatnhar.ui.components.AdminBottomNavigationBar
import com.application.sarmyatnhar.ui.theme.SarMyatNharTheme
import com.application.sarmyatnhar.ui.theme.primaryColor
import com.application.sarmyatnhar.ui.theme.secondaryColor
import com.application.sarmyatnhar.ui.theme.whiteColor
import kotlinx.coroutines.launch

class CoinRequestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SarMyatNharTheme {
                CoinRequestScreen()
            }
        }
    }
}

// Data class to represent a coin request
data class CoinRequest(
    val id: Int,
    val username: String,
    val phone: String,
    val profileImageUrl: String,
    val amount: Int,
    val screenshotUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinRequestScreen() {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    var selectedItem by remember { mutableStateOf("Coin Requests") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showImageDialog by remember { mutableStateOf<String?>(null) }
    var pendingRequest by remember { mutableStateOf<com.application.sarmyatnhar.data.entity.PaymentRequest?>(null) }
    var isApproveAction by remember { mutableStateOf(false) }
    var coinRequests by remember { mutableStateOf<List<CoinRequest>>(emptyList()) }
    var paymentRequests by remember { mutableStateOf<List<com.application.sarmyatnhar.data.entity.PaymentRequest>>(emptyList()) }

    LaunchedEffect(Unit) {
        val db = com.application.sarmyatnhar.data.AppDatabase.getDatabase(context)
        val paymentRequestDao = db.paymentRequestDao()
        val paymentMethodDao = db.paymentMethodDao()
        val userDao = db.userDao()
        paymentRequests = paymentRequestDao.getByStatus("pending")
        val requests = mutableListOf<CoinRequest>()
        for (pr in paymentRequests) {
            val paymentMethod = pr.payment_method_id?.let { paymentMethodDao.getByUser(it) }
            val user = paymentMethod?.user_id?.let { userDao.getUserById(it) }
            if (user != null && paymentMethod != null) {
                requests.add(
                    CoinRequest(
                        id = pr.id,
                        username = user.username,
                        phone = user.phone ?: "",
                        profileImageUrl = user.profile_url ?: "",
                        amount = pr.coins ?: 0,
                        screenshotUrl = pr.proof_image_url
                    )
                )
            }
        }
        coinRequests = requests
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Admin Panel - Coin Requests",
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
                    // TODO: Handle navigation
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
            items(coinRequests) { request ->
                CoinRequestItem(
                    request = request,
                    onApproveClick = {
                        pendingRequest = paymentRequests.find { it.id == request.id }
                        isApproveAction = true
                        showConfirmDialog = true
                    },
                    onRejectClick = {
                        pendingRequest = paymentRequests.find { it.id == request.id }
                        isApproveAction = false
                        showConfirmDialog = true
                    },
                    onScreenshotClick = {
                        showImageDialog = request.screenshotUrl
                    }
                )
            }
        }
    }

    // Confirmation Dialog
    if (showConfirmDialog) {
        val message = if (isApproveAction) {
            "Are you sure you want to approve this request?"
        } else {
            "Are you sure you want to reject this request?"
        }
        ConfirmDialog(
            message = message,
            onResult = { confirmed ->
                if (confirmed) {
                    val db = com.application.sarmyatnhar.data.AppDatabase.getDatabase(context)
                    val paymentRequestDao = db.paymentRequestDao()
                    val paymentMethodDao = db.paymentMethodDao()
                    val walletDao = db.walletDao()
                    val userDao = db.userDao()
                    val action = if (isApproveAction) "approved" else "rejected"
                    val request = pendingRequest
                    (context as? ComponentActivity)?.lifecycleScope?.launch {
                        if (request != null) {
                            paymentRequestDao.updateStatus(request.id, action, System.currentTimeMillis())
                            if (action == "approved") {
                                val paymentMethod = request.payment_method_id?.let { paymentMethodDao.getByUser(it) }
                                val userId = paymentMethod?.user_id
                                if (userId != null && request.coins != null) {
                                    val wallet = walletDao.getByUser(userId)
                                    val newReadPoints = (wallet?.read_points ?: 0) + request.coins
                                    walletDao.updateReadPoints(userId, newReadPoints, System.currentTimeMillis())
                                }
                            }
                            Toast.makeText(context, "Request has been $action.", Toast.LENGTH_SHORT).show()
                            // Refresh requests
                            paymentRequests = paymentRequestDao.getByStatus("pending")
                            val requests = mutableListOf<CoinRequest>()
                            for (pr in paymentRequests) {
                                val paymentMethod = pr.payment_method_id?.let { paymentMethodDao.getByUser(it) }
                                val user = paymentMethod?.user_id?.let { userDao.getUserById(it) }
                                if (user != null && paymentMethod != null) {
                                    requests.add(
                                        CoinRequest(
                                            id = pr.id,
                                            username = user.username,
                                            phone = user.phone ?: "",
                                            profileImageUrl = user.profile_url ?: "",
                                            amount = pr.coins ?: 0,
                                            screenshotUrl = pr.proof_image_url
                                        )
                                    )
                                }
                            }
                            coinRequests = requests
                        }
                    }
                }
                showConfirmDialog = false
                pendingRequest = null
            }
        )
    }

    // Image Lightbox Dialog
    if (showImageDialog != null) {
        Dialog(onDismissRequest = { showImageDialog = null }) {
            AsyncImage(
                model = showImageDialog,
                contentDescription = "Payment Screenshot",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        }
    }
}

@Composable
fun CoinRequestItem(
    request: CoinRequest,
    onApproveClick: (CoinRequest) -> Unit,
    onRejectClick: (CoinRequest) -> Unit,
    onScreenshotClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = whiteColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // User Info
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                AsyncImage(
                    model = request.profileImageUrl,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(secondaryColor)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = request.username,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = request.phone,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            // Coin Amount and Screenshot
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(0.5f)
            ) {
                Text(
                    text = "${request.amount} Coins",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = request.screenshotUrl,
                    contentDescription = "Payment Screenshot",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onScreenshotClick(request.screenshotUrl) }
                )
            }
        }

        Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = { onApproveClick(request) },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                modifier = Modifier.width(120.dp)
            ) {
                Text("Approve", color = Color.White, fontSize = 12.sp)
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

@Preview(showBackground = true)
@Composable
fun PreviewCoinRequestScreen() {
    CoinRequestScreen()
}