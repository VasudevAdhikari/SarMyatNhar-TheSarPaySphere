package com.application.sarmyatnhar.ui.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.platform.LocalContext
import com.application.sarmyatnhar.ui.activities.CoinActivity
import com.application.sarmyatnhar.ui.activities.NotificationActivity
import com.application.sarmyatnhar.ui.theme.primaryColor
import androidx.compose.runtime.LaunchedEffect
import com.application.sarmyatnhar.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HeaderSection() {
    val context = LocalContext.current
    var showSearchDialog by remember { mutableStateOf(false) }
    var readPoints by remember { mutableStateOf(0) }
    var incomePoints by remember { mutableStateOf(0) }

    // Fetch points from DB
    LaunchedEffect(Unit) {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", android.content.Context.MODE_PRIVATE)
        val email = sharedPref.getString("user", null)
        if (email != null) {
            val db = AppDatabase.getDatabase(context)
            val user = db.userDao().getUserDetailsByEmail(email)
            user?.let {
                val wallet = db.walletDao().getByUser(it.id)
                readPoints = wallet?.read_points ?: 0
                incomePoints = wallet?.income_points ?: 0
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left side: Home icon and title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color = primaryColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home Icon",
                    modifier = Modifier.size(24.dp),
                    tint = primaryColor
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Home",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = primaryColor
            )
        }

        // Right side: Search, Notifications, and Points
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Search Icon with circular background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color = primaryColor.copy(alpha = 0.2f))
                    .clickable { showSearchDialog = true }, // Set state to true on click
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(24.dp),
                    tint = primaryColor
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            // Notifications Icon with no background
            Box(
                modifier = Modifier.clickable {
                    Toast.makeText(
                        context,
                        "Not Implemented Yet. Only Dummy Data",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(context, NotificationActivity::class.java)
                    context.startActivity(intent)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier.size(24.dp),
                    tint = primaryColor
                )
                // Red dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            // Gold Dollar with points
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color = Color(0xFFFFD700)).clickable {
                            val intent = Intent(context, CoinActivity::class.java)
                            context.startActivity(intent)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = readPoints.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            // Silver Dollar with points
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color = Color(0xFFC0C0C0))
                        .clickable {
                            val intent = Intent(context, CoinActivity::class.java)
                            context.startActivity(intent)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = incomePoints.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    // Show the search dialog if the state variable is true
    if (showSearchDialog) {
        SearchDialog(
            onDismissRequest = { showSearchDialog = false },
            onSearchClicked = { query ->
                val intent = Intent(context, com.application.sarmyatnhar.ui.activities.BookListActivity::class.java)
                intent.putExtra("PARAMETER", "search:$query")
                context.startActivity(intent)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHeaderSection() {
    HeaderSection()
}