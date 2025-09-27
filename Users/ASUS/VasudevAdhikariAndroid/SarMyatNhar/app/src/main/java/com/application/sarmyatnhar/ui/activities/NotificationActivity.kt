package com.application.sarmyatnhar.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.application.sarmyatnhar.R // Assuming you have an R file with drawables
import com.application.sarmyatnhar.ui.theme.SarMyatNharTheme
import com.application.sarmyatnhar.ui.theme.accentColor
import com.application.sarmyatnhar.ui.theme.primaryColor
import com.application.sarmyatnhar.ui.theme.secondaryColor

data class NotificationItem(val title: String, val message: String, val imageUrl: String? = null)

enum class NotificationType { NEWS, OFFERS }

val poppinsFontFamily = FontFamily(
    Font(R.font.poppins_regular),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

val sampleNewsItems = listOf(
    NotificationItem(
        title = "Successfully exchanged 100 coins.",
        message = "Thank you for your purchase! For every 1,000 coins, you'll receive an extra 50 coins for free.",
    ),
    NotificationItem(
        title = "Zat Kyi Sal Bwae",
        message = "Experience 10 unforgettable stories in one powerful book — Zat Kyi 10 Bwe is out now!",
        imageUrl = "https://images.squarespace-cdn.com/content/v1/624da83e75ca872f189ffa42/aa45e942-f55d-432d-8217-17c7d98105ce/image001.jpg"
    )
)

val sampleOfferItems = listOf(
    NotificationItem(
        title = "Your book Zat Kyi 10 Bwe has been successfully approved. Congratulations, and wishing you great success with your publication!",
        message = ""
    ),
    NotificationItem(
        title = "Thank you for submitting your book Zat Kyi 10 Bwe. Unfortunately, we are unable to approve it at this time due to pricing concerns. Please review and adjust the price before resubmitting.",
        message = ""
    )
)

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SarMyatNharTheme {
                NotificationScreen()
            }
        }
    }
}

@Composable
fun NotificationScreen() {
    var selectedTab by remember { mutableStateOf(NotificationType.NEWS) }

    Scaffold(
        topBar = { NotificationTopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            TabSection(selectedTab) { newTab ->
                selectedTab = newTab
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (selectedTab == NotificationType.NEWS) {
                NotificationList(items = sampleNewsItems)
            } else {
                NotificationList(items = sampleOfferItems)
            }
        }
    }
}

@Composable
fun NotificationTopBar() {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            activity?.finish()
        }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = primaryColor
            )
        }
        Text(
            text = "Notification",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            fontFamily = poppinsFontFamily,
            color = Color.Black,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun TabSection(
    selectedTab: NotificationType,
    onTabSelected: (NotificationType) -> Unit
) {

    val announcementPainter: Painter = rememberVectorPainter(Icons.Default.Announcement)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(accentColor)
            .padding(4.dp)
    ) {
        TabItem(
            icon = painterResource(id = R.drawable.ic_notification),
            text = "News",
            isSelected = selectedTab == NotificationType.NEWS,
            onClick = { onTabSelected(NotificationType.NEWS) }
        )
        TabItem(
            icon = announcementPainter,
            text = "Offers",
            isSelected = selectedTab == NotificationType.OFFERS,
            onClick = { onTabSelected(NotificationType.OFFERS) }
        )
    }
}

@Composable
fun RowScope.TabItem(
    icon: Painter,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(if (isSelected) primaryColor else Color.Transparent)
            .padding(vertical = 12.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = text,
                tint = if (isSelected) Color.White else primaryColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = if (isSelected) Color.White else primaryColor,
                fontWeight = FontWeight.Medium,
                fontFamily = poppinsFontFamily
            )
        }
    }
}

@Composable
fun NotificationList(items: List<NotificationItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            NotificationCard(item = item)
        }
    }
}

@Composable
fun NotificationCard(item: NotificationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = accentColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.imageUrl != null) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notification),
                    contentDescription = null,
                    tint = secondaryColor,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = poppinsFontFamily,
                    color = primaryColor
                )
                if (item.message.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.message,
                        fontSize = 14.sp,
                        fontFamily = poppinsFontFamily,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNotificationScreen() {
    SarMyatNharTheme {
        NotificationScreen()
    }
}
