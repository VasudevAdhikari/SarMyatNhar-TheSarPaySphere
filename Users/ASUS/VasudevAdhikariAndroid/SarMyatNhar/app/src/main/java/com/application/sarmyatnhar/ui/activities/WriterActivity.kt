package com.application.sarmyatnhar.ui.activities

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import coil.compose.AsyncImage
import com.application.sarmyatnhar.ui.components.BottomNavigationBar
import com.application.sarmyatnhar.ui.theme.SarMyatNharTheme
import com.application.sarmyatnhar.ui.theme.accentColor
import com.application.sarmyatnhar.ui.theme.primaryColor
import com.application.sarmyatnhar.ui.theme.whiteColor
import com.application.sarmyatnhar.ui.theme.gradientBrush
import kotlin.properties.Delegates
import androidx.lifecycle.lifecycleScope
import com.application.sarmyatnhar.data.AppDatabase
import com.application.sarmyatnhar.data.entity.Book
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

// Data class now includes status
data class Book1(val id: Int, val title: String, val coverUrl: String, val status: String)
lateinit var sharedPref: SharedPreferences
lateinit var token: String
var isLoggedIn by Delegates.notNull<Boolean>()

// Global variables for URIs
var coverUri: Uri? = null
var fileUri: Uri? = null
lateinit var fileName: String

class WriterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SarMyatNharTheme {
                WriterScreen()
            }
        }
        sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        token = sharedPref.getString("user", "").toString() // null if not found
        isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
    }
}

@Composable
fun BookItem(book: Book1, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    val statusColor = when (book.status.lowercase()) {
        "pending" -> Color(0xFFDAA520)
        "rejected" -> Color.Red
        "approved" -> Color.Green
        else -> Color.Gray
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
    ) {
        Box(
            modifier = Modifier
                .clickable(onClick = onClick)
        ) {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = "Book1 cover for ${book.title}",
                modifier = Modifier
                    .size(190.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )

            // Status Capsule
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(statusColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = book.status,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
            }

            // Delete button with accent color circle
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDAA520)) // Accent Color
                    .clickable(onClick = onDeleteClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete book",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = book.title,
            color = primaryColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif
        )
    }
}

@Composable
fun WriterScreen() {
    val context = LocalContext.current
    var selectedItem by remember { mutableStateOf("Home") }
    var allBooks by remember { mutableStateOf<List<Book>>(emptyList()) }
    LaunchedEffect(Unit) {
        val db = AppDatabase.getDatabase(context)
        val bookDao = db.bookDao()
        allBooks = bookDao.getBooksByAuthorEmail(token)
    }
    val yourBooks: List<Book1> = allBooks.map {
        Book1(
            id = it.id,
            title = it.title,
            coverUrl = it.thumbnail_url ?: "",
            status = it.status
        )
    }

    val activity = context as? ComponentActivity
    var showUploadDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showPostConfirmDialog by remember { mutableStateOf(false) } // Correctly declared as state
    var bookToDelete by remember { mutableStateOf<Book1?>(null) }
    var postBookData by remember { mutableStateOf<Quad<String, String, String, Int>?>(null) }


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
                Text(
                    text = "Author Panel",
                    color = primaryColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Your Books Section
            Text(
                text = "Your Books",
                color = primaryColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(24.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(yourBooks) { book ->
                    BookItem(book,
                        onClick = {
                            // Navigate to BookDetailsActivity
                            Toast.makeText(context, "Navigating to Book1 Details for ${book.title}", Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, BookDetailsActivity::class.java)
                            intent.putExtra("BOOK_ID", "1")
                            context.startActivity(intent)
                        },
                        onDeleteClick = {
                            bookToDelete = book
                            showConfirmDialog = true
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Upload New Book Button
            Button(
                onClick = { showUploadDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Upload new Book")
            }
        }
    }

    if (showUploadDialog) {
        UploadBookDialog(
            onDismiss = { showUploadDialog = false },
            onPostRequest = { title, category, description, price ->
                postBookData = Quad(title, category, description, price)
                showPostConfirmDialog = true
                showUploadDialog = false
            }
        )
    }

    if (showConfirmDialog && bookToDelete != null) {
        ConfirmDialog(
            message = "Are you sure you want to delete '${bookToDelete!!.title}'?",
            onResult = { confirmed ->
                if (confirmed) {
                    val db = AppDatabase.getDatabase(context)
                    val bookDao = db.bookDao()
                    (context as? ComponentActivity)?.lifecycleScope?.launch {
                        bookDao.deleteBookById(bookToDelete!!.id)
                        Toast.makeText(context, "Deleted book: ${bookToDelete!!.title}", Toast.LENGTH_SHORT).show()
                    }
                }
                showConfirmDialog = false
                bookToDelete = null
            }
        )
    }

    if (showPostConfirmDialog && postBookData != null) {
        val (title, category, description, price) = postBookData!!
        ConfirmDialog(
            message = "Are you sure you want to post the book titled '$title'?",
            onResult = { confirmed ->
                if (confirmed) {
                    val db = AppDatabase.getDatabase(context)
                    val userDao = db.userDao()
                    val authorProfileDao = db.authorProfileDao()
                    val bookDao = db.bookDao()
                    (context as? ComponentActivity)?.lifecycleScope?.launch {
                        val user = userDao.getUserByEmail(token)
                        if (user != null) {
                            val authorProfile = authorProfileDao.getOrCreateAuthorProfile(user.id, user.username, null)
                            // Save cover image to internal storage
                            var savedCoverPath: String? = null
                            coverUri?.let { uri ->
                                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                                val file = File(context.filesDir, "cover_${System.currentTimeMillis()}.jpg")
                                inputStream?.use { input ->
                                    FileOutputStream(file).use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                savedCoverPath = file.absolutePath
                            }
                            // Save book file to internal storage
                            var savedFilePath: String? = null
                            fileUri?.let { uri ->
                                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                                val file = File(context.filesDir, "book_${System.currentTimeMillis()}.pdf")
                                inputStream?.use { input ->
                                    FileOutputStream(file).use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                savedFilePath = file.absolutePath
                            }
                            val newBook = Book(
                                uploader_id = authorProfile.id,
                                title = title,
                                category = category,
                                description = description,
                                file_url = savedFilePath ?: "",
                                price_points = price,
                                status = "pending",
                                thumbnail_url = savedCoverPath
                            )
                            bookDao.insert(newBook)
                            Toast.makeText(context, "Book posted successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "User not found.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                showPostConfirmDialog = false
                postBookData = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadBookDialog(onDismiss: () -> Unit, onPostRequest: (String, String, String, Int) -> Unit) {
    var title by remember { mutableStateOf("") }
    val categories = listOf(
        "Fiction", "NonFiction", "SciFi", "Fantasy", "Mystery", "Thriller",
        "Romance", "History", "Poetry", "Drama", "Comics", "Manga",
        "Children", "YoungAdult", "Biography", "Memoir", "SelfHelp", "Philosophy",
        "Psychology", "Religion", "Politics", "Science", "Math", "Medical",
        "Business", "Education", "Travel", "Cooking", "Art", "Law"
    )

    var category by remember { mutableStateOf(categories[0]) }
    var expanded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    val context = LocalContext.current

    val bookCoverLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            coverUri = uri
        }
    )

    val bookFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            fileUri = uri
            uri?.let {
                // Get the file name from the Uri
                val cursor = context.contentResolver.query(it, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            fileName = it.getString(nameIndex)
                        }
                    }
                }
            }
        }
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(12.dp),
            color = accentColor
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                // Dialog Title
                Text(
                    text = "Upload a New Book",
                    color = primaryColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Book Title
                    Text(text = "Book Title", color = primaryColor, fontFamily = FontFamily.SansSerif)
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Book Category
                    Text(text = "Book Category", color = primaryColor, fontFamily = FontFamily.SansSerif)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            readOnly = true,
                            value = category,
                            onValueChange = {},
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        category = selectionOption
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Book Description
                    Text(text = "Book Description", color = primaryColor, fontFamily = FontFamily.SansSerif)
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Book Cover
                    Column {
                        Text(text = "Book Cover", color = primaryColor, fontFamily = FontFamily.SansSerif)
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(onClick = { bookCoverLauncher.launch("image/*") }) {
                            Text("Choose from Gallery")
                        }
                        if (coverUri != null) {
                            AsyncImage(
                                model = coverUri,
                                contentDescription = "Selected book cover preview",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 8.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Book File
                    Column {
                        Text(text = "Book File", color = primaryColor, fontFamily = FontFamily.SansSerif)
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(onClick = { bookFileLauncher.launch("application/pdf") }) {
                            Text("Choose from Files")
                        }
                        if (fileUri != null) {
                            Text(
                                text = "File selected: $fileName",
                                color = primaryColor,
                                fontFamily = FontFamily.SansSerif,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Book Price
                    Text(text = "Price in Coins", color = primaryColor, fontFamily = FontFamily.SansSerif)
                    TextField(
                        value = price,
                        onValueChange = { if (it.all { ch -> ch.isDigit() }) price = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = primaryColor, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onPostRequest(title, category, description, price.toIntOrNull() ?: 0)
                    }) {
                        Text("Post", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWriterScreen() {
    SarMyatNharTheme {
        WriterScreen()
    }
}

// Helper Quad class
class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D) {
    operator fun component1() = first
    operator fun component2() = second
    operator fun component3() = third
    operator fun component4() = fourth
}
