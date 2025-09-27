package com.application.sarmyatnhar.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.application.sarmyatnhar.ui.theme.SarMyatNharTheme
import com.application.sarmyatnhar.ui.theme.gradientBrush
import com.application.sarmyatnhar.ui.theme.primaryColor
import com.application.sarmyatnhar.ui.components.ConfirmDialog
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates

// Placeholder color constants (replace with your colors from colors.kt)
val primaryColor = Color(0xFF463FA6) // A deep purple
val secondaryColor = Color(0xFF6C63FF) // A lighter purple
val accentColor = Color(0xFFB9CCEB) // A light cyan
val fontColor = Color.Black
val goldenColor = Color(0xFFFFD700)

var bookId by Delegates.notNull<Int>()

class BookDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bookDetails: BookDetails? = null
        bookId = intent.getIntExtra("BOOK_ID", 0)
        if (bookId != 0) {
            val db = com.application.sarmyatnhar.data.AppDatabase.getDatabase(this)
            val bookDao = db.bookDao()
            val authorProfileDao = db.authorProfileDao()
            val book = runBlocking { bookDao.getBookById(bookId) }
            if (book != null) {
                val authorProfile = book.uploader_id?.let { runBlocking { authorProfileDao.getAuthorById(it) } }
                bookDetails = BookDetails(
                    title = book.title,
                    author = authorProfile?.pen_name ?: "",
                    coverImageUrl = book.thumbnail_url ?: "",
                    views = "0",
                    rating = "-",
                    genre = book.category.toString(),
                    points = book.price_points.toString(),
                    description = book.description ?: "",
                    isSaved = false
                )
            }
        }
        setContent {
            SarMyatNharTheme {
                if (bookDetails != null) {
                    BookDetailsScreen(bookDetails)
                } else {
                    Text("Book not found. $bookId")
                }
            }
        }
    }
}

// Data class to represent the book details
data class BookDetails(
    val title: String,
    val author: String,
    val coverImageUrl: String,
    val views: String,
    val rating: String,
    val genre: String,
    val points: String,
    val description: String,
    var isSaved: Boolean = false // Added state for the saved icon
)

// Dummy data for preview
val dummyBookDetails = BookDetails(
    title = "Wuthering heights",
    author = "by Emily Brontë",
    coverImageUrl = "https://images.squarespace-cdn.com/content/v1/624da83e75ca872f189ffa42/aa45e942-f55d-432d-8217-17c7d98105ce/image001.jpg",
    views = "0",
    rating = "7/10",
    genre = "Novel",
    points = "100",
    description = "Wuthering Heights by Emily Brontë is a dark and powerful Gothic novel that explores themes of love, revenge, passion, and the destructive nature of obsession. Set on the Yorkshire moors, the story follows the turbulent relationship between Heathcliff, a brooding and mysterious outsider, and Catherine Earnshaw, whose intense but conflicted love for him shapes the fate of two generations. Through its haunting atmosphere, complex characters, and shifting narrators, the novel portrays how deep emotions can both elevate and destroy, making it a timeless tale of human passion and tragedy."
)

@Composable
fun BookDetailsScreen(book: BookDetails = dummyBookDetails) {
    var showDownloadConfirmation by remember { mutableStateOf(false) }
    var isBookSaved by remember { mutableStateOf(book.isSaved) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            BookDetailsTopBar(
                points = book.points,
                isBookSaved = isBookSaved,
                onSaveClicked = { 
                    isBookSaved = it
                    if (it) {
                        val sharedPref = context.getSharedPreferences("MyAppPrefs", android.content.Context.MODE_PRIVATE)
                        val email = sharedPref.getString("user", null)
                        if (email != null) {
                            val db = com.application.sarmyatnhar.data.AppDatabase.getDatabase(context)
                            val userDao = db.userDao()
                            val libraryDao = db.libraryDao()
                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                val user = userDao.getUserByEmail(email)
                                if (user != null) {
                                    libraryDao.insertLibrary(user.id, bookId, false)
                                }
                            }
                        }
                        Toast.makeText(context, "Saved successfully.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Unsaved from your library.", Toast.LENGTH_SHORT).show()
                    }
                },
                onDownloadClicked = { showDownloadConfirmation = true } // Show dialog on download click
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(brush = gradientBrush)
        ) {
            BookCoverSection(book)
            Spacer(modifier = Modifier.height(32.dp))
            DescriptionSection(book.description)
        }
    }

    if (showDownloadConfirmation) {
        val context = LocalContext.current
        ConfirmDialog(
            message = "Are you sure you want to download this book? This book costs ${book.points} points which will be deducted from your wallet.",
            onResult = { confirmed ->
                if (confirmed) {
                    val sharedPref = context.getSharedPreferences("MyAppPrefs", android.content.Context.MODE_PRIVATE)
                    val email = sharedPref.getString("user", null)
                    if (email != null) {
                        val db = com.application.sarmyatnhar.data.AppDatabase.getDatabase(context)
                        val userDao = db.userDao()
                        val walletDao = db.walletDao()
                        val authorProfileDao = db.authorProfileDao()
                        val libraryDao = db.libraryDao()
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                            val user = userDao.getUserByEmail(email)
                            val wallet = user?.id?.let { walletDao.getByUser(it) }
                            val book = if (bookId != 0) db.bookDao().getBookById(bookId) else null
                            if (user != null && wallet != null && book != null) {
                                val pricePoints = book.price_points
                                if (wallet.read_points < pricePoints) {
                                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                        android.widget.Toast.makeText(context, "Not enough balance.", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    // Deduct from user's wallet
                                    val newReadPoints = wallet.read_points - pricePoints
                                    walletDao.updateReadPoints(user.id, newReadPoints, System.currentTimeMillis())
                                    // Add to author's wallet
                                    val authorProfile = book.uploader_id?.let { authorProfileDao.getAuthorById(it) }
                                    val authorUserId = authorProfile?.user_id
                                    if (authorUserId != null) {
                                        val authorWallet = walletDao.getByUser(authorUserId)
                                        val newIncomePoints = (authorWallet?.income_points ?: 0) + pricePoints
                                        walletDao.updateIncomePoints(authorUserId, newIncomePoints, System.currentTimeMillis())
                                    }
                                    // Add to Library
                                    libraryDao.insert(com.application.sarmyatnhar.data.entity.Library(
                                        user_id = user.id,
                                        book_id = book.id,
                                        is_downloaded = true
                                    ))
                                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                        android.widget.Toast.makeText(context, "Downloading...", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Download canceled.", Toast.LENGTH_SHORT).show()
                }
                showDownloadConfirmation = false
            }
        )
    }
}

@Composable
fun BookDetailsTopBar(
    points: String,
    isBookSaved: Boolean,
    onSaveClicked: (Boolean) -> Unit,
    onDownloadClicked: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(accentColor),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Back Button
        IconButton(onClick = {
            activity?.finish()
        }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = primaryColor,
                modifier = Modifier.size(24.dp)
            )
        }

        // Action Icons
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    onSaveClicked(!isBookSaved)
                    if (isBookSaved) {
                        Toast.makeText(context, "Unsaved from your library.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Saved successfully.", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Icon(
                    imageVector = if (isBookSaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = secondaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = onDownloadClicked) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download",
                    tint = secondaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(goldenColor)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clickable{
                        val intent = Intent(context, CoinActivity::class.java)
                        context.startActivity(intent)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = points,
                    fontSize = 14.sp,
                    color = primaryColor
                )
            }
        }
    }
}

@Composable
fun BookCoverSection(book: BookDetails) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = book.coverImageUrl,
            contentDescription = book.title,
            modifier = Modifier
                .size(175.dp, 250.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = book.title,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = fontColor
        )
        Text(
            text = book.author,
            fontSize = 16.sp,
            color = primaryColor
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            BookInfoChip(icon = Icons.Default.Book, text = book.genre)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val intent = Intent(context, CoinActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .width(150.dp)
                .clip(RoundedCornerShape(24.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = "Buy Coin",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Buy Coin", color = Color.White)
        }
    }
}

@Composable
fun BookInfoChip(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(accentColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = fontColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = fontColor
        )
    }
}

@Composable
fun DescriptionSection(description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 22.dp)
    ) {
        Text(
            text = "Description",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = fontColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            fontSize = 14.sp,
            color = fontColor,
            lineHeight = 20.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBookDetailsScreen() {
    SarMyatNharTheme {
        BookDetailsScreen()
    }
}
