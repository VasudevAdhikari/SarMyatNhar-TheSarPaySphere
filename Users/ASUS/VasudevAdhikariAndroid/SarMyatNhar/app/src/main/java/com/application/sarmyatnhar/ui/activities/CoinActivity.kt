package com.application.sarmyatnhar.ui.activities

import android.content.Context
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradient
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
import com.application.sarmyatnhar.ui.theme.SarMyatNharTheme
import com.application.sarmyatnhar.ui.theme.blackColor
import com.application.sarmyatnhar.ui.theme.silverColor
import com.application.sarmyatnhar.ui.theme.whiteColor
import com.application.sarmyatnhar.ui.theme.primaryColor
import com.application.sarmyatnhar.ui.theme.accentColor
import com.application.sarmyatnhar.ui.theme.goldenColor
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun PoppinsText(text: String, color: Color = Color.Black, fontSize: Int = 16, fontWeight: FontWeight = FontWeight.Normal) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize.sp,
        fontWeight = fontWeight,
        fontFamily = FontFamily.SansSerif // Using a generic font as a placeholder
    )
}

// Defining the gradientBrush here so it can be used in the popup background
val gradientBrush = Brush.linearGradient(
    colors = listOf(primaryColor, accentColor)
)

class CoinActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SarMyatNharTheme {
                CoinScreen()
            }
        }
    }
}


@Composable
fun CoinScreen() {
    val context = LocalContext.current
    var showBuyPopup by remember { mutableStateOf(false) }
    var selectedBuyCoins by remember { mutableStateOf("") }
    var selectedBuyPrice by remember { mutableStateOf("") }
    var showSellPopup by remember { mutableStateOf(false) }
    var selectedSellCoins by remember { mutableStateOf("") }
    var selectedSellPrice by remember { mutableStateOf("") }
    var readerCoins by remember { mutableStateOf(0) }
    var authorCoins by remember { mutableStateOf(0) }
    val activity = context as? ComponentActivity
    LaunchedEffect(Unit) {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("user", null)
        if (email != null) {
            val db = com.application.sarmyatnhar.data.AppDatabase.getDatabase(context)
            val userDao = db.userDao()
            val walletDao = db.walletDao()
            val user = userDao.getUserByEmail(email)
            val wallet = user?.id?.let { walletDao.getByUser(it) }
            readerCoins = wallet?.read_points ?: 0
            authorCoins = wallet?.income_points ?: 0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = whiteColor)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = blackColor,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        activity?.finish()
                    }
            )

            Spacer(modifier = Modifier.width(16.dp))
            PoppinsText(
                text = "Coin Buy and Sell",
                color = primaryColor,
                fontSize = 20,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Reader and Author Coins
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            CoinBox(
                title = "Reader Coins",
                coinValue = readerCoins.toString(),
                coinColor = goldenColor
            )
            CoinBox(
                title = "Author Coins",
                coinValue = authorCoins.toString(),
                coinColor = silverColor
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Buy Reader Coins Section
        PoppinsText(
            text = "Buy Reader Coins",
            color = blackColor,
            fontSize = 18,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val coinsList = listOf("100", "300", "500", "700", "1000", "1500")
            val pricesList = listOf("5,000 mmk", "14,000 mmk", "22,000 mmk", "30,000 mmk", "43,000 mmk", "65,000 mmk")
            items(coinsList.size) { index ->
                BuyCoinItem(
                    coins = coinsList[index],
                    price = pricesList[index],
                    onItemClick = { coins, price ->
                        selectedBuyCoins = coins
                        selectedBuyPrice = price
                        showBuyPopup = true
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Trade Author's Coins Section
        PoppinsText(
            text = "Trade Author's Coins",
            color = blackColor,
            fontSize = 18,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tradeList = listOf(
                Pair("500 Coins", "5,000 mmk"),
                Pair("1000 Coins", "10,000 mmk"),
                Pair("2000 Coins", "20,000 mmk"),
                Pair("5000 Coins", "50,000 mmk"),
                Pair("10000 Coins", "100,000 mmk")
            )
            tradeList.forEach { (coins, price) ->
                TradeCoinItem(
                    coins = coins,
                    price = price,
                    onItemClick = {
                        selectedSellCoins = coins
                        selectedSellPrice = price
                        showSellPopup = true
                    }
                )
            }
        }
    }

    if (showBuyPopup) {
        BuyRequestPopup(
            coins = selectedBuyCoins.toInt(),
            amountMoney = selectedBuyPrice.replace(Regex("[^0-9]"), "").toDouble(),
            onDismiss = { showBuyPopup = false }
        )
    }

    if (showSellPopup) {
        ConfirmDialog(
            message = "Are you sure you want to make a Coin Sell Request with $selectedSellCoins for $selectedSellPrice?",
            onResult = { confirmed ->
                if (confirmed) {
                    val sellAmount = selectedSellCoins.replace(Regex("[^0-9]"), "").toInt()
                    if (authorCoins >= sellAmount) {
                        authorCoins -= sellAmount
                        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                        val email = sharedPref.getString("user", null)
                        if (email != null) {
                            val db = com.application.sarmyatnhar.data.AppDatabase.getDatabase(context)
                            val userDao = db.userDao()
                            val paymentMethodDao = db.paymentMethodDao()
                            val conversionRequestDao = db.conversionRequestDao()
                            (context as? ComponentActivity)?.lifecycleScope?.launch {
                                val user = userDao.getUserByEmail(email)
                                val paymentMethod = user?.id?.let { paymentMethodDao.getPaymentMethodByUserId(it) }
                                val paymentMethodId = paymentMethod?.id
                                if (paymentMethodId != null) {
                                    val conversionRequest = com.application.sarmyatnhar.data.entity.ConversionRequest(
                                        payment_method_id = paymentMethodId,
                                        type = "income_to_money",
                                        amount_points = sellAmount,
                                        payout_proof_url = null,
                                        status = "pending",
                                        message = null
                                    )
                                    conversionRequestDao.insert(conversionRequest)
                                }
                            }
                        }
                        Toast.makeText(context, "Coin Sell request sent to the admin.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Not enough coins to make sell coin request.", Toast.LENGTH_SHORT).show()
                    }
                }
                showSellPopup = false
            }
        )
    }
}

@Composable
fun CoinBox(title: String, coinValue: String, coinColor: Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(primaryColor)
            .padding(16.dp)
    ) {
        PoppinsText(
            text = title,
            color = whiteColor,
            fontSize = 14
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                // Placeholder for coin icon
                imageVector = Icons.Default.CurrencyExchange,
                contentDescription = "Coin",
                tint = coinColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            PoppinsText(
                text = coinValue,
                color = whiteColor,
                fontSize = 18,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BuyCoinItem(coins: String, price: String, onItemClick: (String, String) -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(primaryColor)
            .padding(16.dp)
            .clickable { onItemClick(coins, price) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            // Placeholder for golden coin icon
            imageVector = Icons.Default.CurrencyExchange,
            contentDescription = "Golden Coin",
            tint = goldenColor,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        PoppinsText(
            text = coins,
            color = whiteColor,
            fontSize = 18,
            fontWeight = FontWeight.Bold
        )
        PoppinsText(
            text = price,
            color = whiteColor,
            fontSize = 12
        )
    }
}

@Composable
fun TradeCoinItem(coins: String, price: String, onItemClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(primaryColor)
            .padding(16.dp)
            .clickable(onClick = onItemClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            // Placeholder for silver coin icon
            imageVector = Icons.Default.CurrencyExchange,
            contentDescription = "Silver Coin",
            tint = silverColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        PoppinsText(
            text = coins,
            color = whiteColor,
            fontSize = 16
        )
        Spacer(modifier = Modifier.weight(1f))
        PoppinsText(
            text = price,
            color = whiteColor,
            fontSize = 16
        )
    }
}

@Composable
fun BuyRequestPopup(coins: Int, amountMoney: Double, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val file = File(context.cacheDir, "temp_image.jpg")
            file.outputStream().use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, out)
            }
            imageUri = Uri.fromFile(file)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp))
                .background(brush = gradientBrush),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .widthIn(min = 300.dp, max = 500.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PoppinsText(
                    text = "Coin Buy Request",
                    fontSize = 20,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
                Spacer(modifier = Modifier.height(16.dp))
                PoppinsText(
                    text = "Coins: $coins",
                    fontSize = 18,
                    color = blackColor
                )
                PoppinsText(
                    text = "Amount: $amountMoney mmk",
                    fontSize = 18,
                    color = blackColor
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUri),
                        contentDescription = "Proof Image Preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        PoppinsText(
                            text = "Money Transaction Screenshot",
                            color = Color.DarkGray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = { cameraLauncher.launch(null) },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        PoppinsText("Take Photo", color = whiteColor)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        PoppinsText("Gallery", color = whiteColor)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        PoppinsText("Cancel", color = whiteColor)
                    }
                    Button(
                        onClick = {
                            if (imageUri != null) {
                                val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                                val email = sharedPref.getString("user", null)
                                if (email != null) {
                                    val db = com.application.sarmyatnhar.data.AppDatabase.getDatabase(context)
                                    val userDao = db.userDao()
                                    val paymentMethodDao = db.paymentMethodDao()
                                    val paymentRequestDao = db.paymentRequestDao()
                                    (context as? ComponentActivity)?.lifecycleScope?.launch {
                                        val user = userDao.getUserByEmail(email)
                                        val paymentMethod = user?.id?.let { paymentMethodDao.getPaymentMethodByUserId(it) }
                                        val paymentMethodId = paymentMethod?.id
                                        if (paymentMethodId != null) {
                                            val proofUrl = imageUri.toString()
                                            val request = com.application.sarmyatnhar.data.entity.PaymentRequest(
                                                payment_method_id = paymentMethodId,
                                                proof_image_url = proofUrl,
                                                amount_money = amountMoney,
                                                coins = coins,
                                                status = "pending",
                                                decision_date = null
                                            )
                                            paymentRequestDao.insert(request)
                                            Toast.makeText(context, "Coin Buying request sent to the admin.", Toast.LENGTH_SHORT).show()
                                            onDismiss()
                                        } else {
                                            Toast.makeText(context, "No payment method found for user.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Please select a proof image.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        PoppinsText("Make request", color = whiteColor)
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    message: String,
    onResult: (Boolean) -> Unit
) {
    Dialog(onDismissRequest = { onResult(false) }) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onResult(false) }) {
                        Text("Cancel", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onResult(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        Text("Okay", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewCoinScreenScreen() {
    SarMyatNharTheme {
        CoinScreen()
    }
}
