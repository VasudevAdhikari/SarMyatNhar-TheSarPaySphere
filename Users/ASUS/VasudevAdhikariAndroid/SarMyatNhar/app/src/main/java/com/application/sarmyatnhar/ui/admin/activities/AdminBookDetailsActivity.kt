package com.application.sarmyatnhar.ui.admin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.application.sarmyatnhar.ui.activities.CoinActivity
import com.application.sarmyatnhar.ui.components.ConfirmDialog
import com.application.sarmyatnhar.ui.theme.SarMyatNharTheme
import com.application.sarmyatnhar.ui.theme.gradientBrush
import com.application.sarmyatnhar.ui.theme.primaryColor
import com.application.sarmyatnhar.ui.theme.secondaryColor
import com.application.sarmyatnhar.ui.theme.accentColor
import com.application.sarmyatnhar.ui.theme.blackColor
import com.application.sarmyatnhar.ui.theme.primaryColor
import com.application.sarmyatnhar.ui.theme.whiteColor

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

class AdminBookDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SarMyatNharTheme {
                AdminBookDetailsScreen()
            }
        }
        val bookId = intent.getStringExtra("BOOK_ID")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookDetailsScreen(book: BookDetails = dummyBookDetails) {
    var showApproveConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            BookDetailsTopBar()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(brush = gradientBrush)
        ) {
            BookCoverSection(book, onApproveClicked = { showApproveConfirmation = true })
            Spacer(modifier = Modifier.height(32.dp))
            DescriptionSection(book.description)
        }
    }

    if (showApproveConfirmation) {
        ConfirmDialog(
            message = "Are you sure you want to approve this book?",
            onResult = { confirmed ->
                if (confirmed) {
                    // TODO: actual approve logic (network/db)
                    Toast.makeText(context, "Book approved.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Approve canceled.", Toast.LENGTH_SHORT).show()
                }
                showApproveConfirmation = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsTopBar() {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = { activity?.finish() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = whiteColor)
    )
}

@Composable
fun BookCoverSection(book: BookDetails, onApproveClicked: () -> Unit) {
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
        Text(text = book.title, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = blackColor)
        Text(text = book.author, fontSize = 16.sp, color = blackColor)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BookInfoChip(icon = Icons.Default.Book, text = book.genre)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* No action for this button in this context */ },
            modifier = Modifier
                .width(150.dp)
                .clip(RoundedCornerShape(24.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = book.points, tint = Color.White)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = book.points, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onApproveClicked() }, // Correctly calls the lambda to trigger the dialog
            modifier = Modifier
                .width(150.dp)
                .clip(RoundedCornerShape(24.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = secondaryColor)
        ) {
            Text(text = "Approve", color = Color.White)
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
            tint = primaryColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = primaryColor
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
            color = primaryColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            fontSize = 14.sp,
            color = primaryColor,
            lineHeight = 20.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAdminBookDetailsScreen() {
    SarMyatNharTheme {
        AdminBookDetailsScreen()
    }
}
