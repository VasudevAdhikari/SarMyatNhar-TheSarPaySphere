package com.application.sarmyatnhar.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.application.sarmyatnhar.data.AppDatabase
import com.application.sarmyatnhar.data.dao.BookDao
import com.application.sarmyatnhar.data.entity.AuthorProfile
import com.application.sarmyatnhar.data.entity.Book
import com.application.sarmyatnhar.ui.components.AdminBottomNavigationBar
import com.application.sarmyatnhar.ui.theme.SarMyatNharTheme
import com.application.sarmyatnhar.ui.theme.primaryColor
import com.application.sarmyatnhar.ui.theme.secondaryColor
import com.application.sarmyatnhar.ui.theme.whiteColor
import kotlinx.coroutines.launch

class BookApprovalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SarMyatNharTheme {
                BookApprovalScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookApprovalScreen() {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    var selectedItem by remember { mutableStateOf("Monetization") }
    var pendingBooks by remember { mutableStateOf<List<Book>>(emptyList()) }
    var authorProfiles by remember { mutableStateOf<Map<Int, AuthorProfile>>(emptyMap()) }
    var userPhones by remember { mutableStateOf<Map<Int, String?>>(emptyMap()) }
    LaunchedEffect(Unit) {
        val db = AppDatabase.getDatabase(context)
        val bookDao = db.bookDao()
        val authorProfileDao = db.authorProfileDao()
        val userDao = db.userDao()
        val books = bookDao.getBooksByStatus("pending")
        val profiles = mutableMapOf<Int, AuthorProfile>()
        val phones = mutableMapOf<Int, String?>()
        for (book in books) {
            val authorProfile = book.uploader_id?.let { authorProfileDao.getAuthorById(it) }
            if (authorProfile != null) {
                profiles[book.id] = authorProfile
                val user = authorProfile.user_id?.let { userDao.getUserById(it) }
                phones[book.id] = user?.phone
            }
        }
        pendingBooks = books
        authorProfiles = profiles
        userPhones = phones
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Book Approval",
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
            items(pendingBooks) { book ->
                val authorProfile = authorProfiles[book.id]
                val phone = userPhones[book.id]
                BookApprovalItem(
                    book = book,
                    penName = authorProfile?.pen_name ?: "",
                    phone = phone ?: "",
                    onApprove = {
                        val db = AppDatabase.getDatabase(context)
                        (context as? ComponentActivity)?.lifecycleScope?.launch {
                            db.bookDao().updateBookStatus(book.id, "approved")
                            Toast.makeText(context, "Book approved!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onReject = {
                        val db = AppDatabase.getDatabase(context)
                        (context as? ComponentActivity)?.lifecycleScope?.launch {
                            db.bookDao().updateBookStatus(book.id, "rejected")
                            Toast.makeText(context, "Book rejected!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BookApprovalItem(book: Book, penName: String, phone: String, onApprove: () -> Unit, onReject: () -> Unit) {
    val context = LocalContext.current
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book Cover
            Image(
                painter = rememberAsyncImagePainter(model = book.thumbnail_url),
                contentDescription = "Book Cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp, 150.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Book and Author Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "by $penName",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Phone: $phone",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${book.price_points} Points",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = secondaryColor
                )
            }
            // Approve/Reject Buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Approve", color = Color.White)
                }
                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Reject", color = Color.White)
                }
            }
        }
    }
}

object ScaffoldMessenger {
    fun show(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBookApprovalScreen() {
    BookApprovalScreen()
}
