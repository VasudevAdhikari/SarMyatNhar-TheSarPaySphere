package com.application.sarmyatnhar.ui.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.application.sarmyatnhar.R
import com.application.sarmyatnhar.ui.components.BottomNavigationBar
import com.application.sarmyatnhar.ui.theme.SarMyatNharTheme
import com.application.sarmyatnhar.ui.theme.gradientBrush
import com.application.sarmyatnhar.ui.theme.primaryColor

// This data class now uses a URL string for the cover
private data class Book(val title: String, val coverUrl: String, val fileUrl: String = "", val id: Int = 0)


class LibraryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SarMyatNharTheme {
                LibraryScreen()
            }
        }
    }
}

@Composable
fun LibraryScreen() {
    val context = LocalContext.current
    var selectedItem by remember { mutableStateOf("Home") }
    val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val email = sharedPref.getString("user", null)
    var bookmarkedBooks by remember { mutableStateOf<List<Book>>(emptyList()) }
    var downloadedBooks by remember { mutableStateOf<List<Book>>(emptyList()) }
    LaunchedEffect(email) {
        if (email != null) {
            val db = com.application.sarmyatnhar.data.AppDatabase.getDatabase(context)
            val userDao = db.userDao()
            val libraryDao = db.libraryDao()
            val bookDao = db.bookDao()
            val user = userDao.getUserByEmail(email)
            if (user != null) {
                val libraryRecords = libraryDao.getByUser(user.id)
                val bookmarked = mutableListOf<Book>()
                val downloaded = mutableListOf<Book>()
                for (record in libraryRecords) {
                    val book = bookDao.getBookById(record.book_id)
                    if (book != null) {
                        val bookItem = Book(
                            id = book.id,
                            title = book.title,
                            coverUrl = book.thumbnail_url ?: "",
                            fileUrl = book.file_url
                        )
                        if (record.is_downloaded) {
                            downloaded.add(bookItem)
                        } else {
                            bookmarked.add(bookItem)
                        }
                    }
                }
                bookmarkedBooks = bookmarked
                downloadedBooks = downloaded
            }
        }
    }
    val activity = context as? ComponentActivity
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
                .background(brush = gradientBrush)
                .padding(16.dp)
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
                    modifier = Modifier.size(24.dp).clickable {
                        activity?.finish()
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                PoppinsText(
                    text = "My Library",
                    color = primaryColor,
                    fontSize = 20,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Bookmarked Books Section
            PoppinsText(
                text = "Bookmarked Books",
                color = primaryColor,
                fontSize = 18,
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(bookmarkedBooks) { book ->
                    BookItem(book) {
                        val intent = Intent(context, BookDetailsActivity::class.java)
                        intent.putExtra("BOOK_ID", book.id)
                        context.startActivity(intent)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Downloaded Books Section
            PoppinsText(
                text = "Downloaded Books",
                color = primaryColor,
                fontSize = 18,
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(downloadedBooks) { book ->
                    BookItem(book) {
                        try {
                            val fileUri = Uri.parse(book.fileUrl)
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(fileUri, "application/pdf")
                                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Cannot open file.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookItem(book: Book, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = book.coverUrl,
            contentDescription = "Book cover for ${book.title}",
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        PoppinsText(
            text = book.title,
            color = primaryColor,
            fontSize = 14,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLibraryScreen() {
    SarMyatNharTheme {
        LibraryScreen()
    }
}
