package com.application.sarmyatnhar.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.application.sarmyatnhar.data.AppDatabase
import com.application.sarmyatnhar.data.entity.Book
import com.application.sarmyatnhar.ui.components.HeaderSection
import com.application.sarmyatnhar.ui.components.Section
import com.application.sarmyatnhar.ui.theme.SarMyatNharTheme
import com.application.sarmyatnhar.ui.components.BottomNavigationBar
import com.application.sarmyatnhar.ui.theme.accentColor
import kotlinx.coroutines.launch

// Dummy data classes for demonstration. Now using a String for imageUrl.
data class BookItem(val id: Int, val title: String, val imageUrl: String)
data class FeedItem(val id: Int, val title: String, val imageUrl: String)

// ViewModel for HomeActivity
class HomeViewModel : androidx.lifecycle.ViewModel() {
    init {
        // In a real app, you would load data from a repository here
    }

    private fun loadNewlyAddedBooks() { /* Load from repository */ }
    private fun loadFreeBooks() { /* Load from repository */ }
    private fun loadCollections() { /* Load from repository */ }
    private fun loadPopularBooks() { /* Load from repository */ }
    private fun loadTopRatedBooks() { /* Load from repository */ }
    private fun loadRandomFeeds() { /* Load from repository */ }
}

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val homeViewModel: HomeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        setContent {
            SarMyatNharTheme {
                HomeScreen(viewModel = homeViewModel)
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val context = LocalContext.current
    var allBooks by remember { mutableStateOf<List<Book>>(emptyList()) }
    LaunchedEffect(Unit) {
        val db = AppDatabase.getDatabase(context)
        val bookDao = db.bookDao()
        allBooks = bookDao.getAllApprovedBooks()// or get all books if needed
    }
    // Section logic
    val newlyAddedBooks = allBooks.map { BookItem(it.id, it.title, it.thumbnail_url ?: "") }
    val freeBooks = allBooks.filter { it.price_points == 0 }.map { BookItem(it.id, it.title, it.thumbnail_url ?: "") }
    val collections = allBooks.filter { it.title.contains("Collection", true) }.map { BookItem(it.id, it.title, it.thumbnail_url ?: "") }
    val popularBooks = allBooks.sortedByDescending { it.price_points }.take(10).map { BookItem(it.id, it.title, it.thumbnail_url ?: "") }
    val topRatedBooks = allBooks.take(10).map { BookItem(it.id, it.title, it.thumbnail_url ?: "") }
    val randomFeeds = allBooks.shuffled().take(5).map {
        FeedItem(it.id, it.title, it.thumbnail_url ?: "")
    }


    // Define the gradient brush
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color.White,
            accentColor,
            Color.White
        ),
        start = Offset.Zero, // top-left corner
        end = Offset.Infinite // bottom-right corner, equivalent to 135 degrees
    )

    // State to track the selected navigation item
    var selectedItem by remember { mutableStateOf("Home") }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { title -> selectedItem = title }
            )
        }
    ) { innerPadding ->
        // The main content of the screen
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
                .padding(innerPadding) // Apply padding from the scaffold
                .padding(8.dp)
        ) {
            // Header Section
            item { HeaderSection() }

            // Banner
            item { BannerSection("https://images.squarespace-cdn.com/content/v1/624da83e75ca872f189ffa42/aa45e942-f55d-432d-8217-17c7d98105ce/image001.jpg") }

            // Sections
            item { Section(title = "Newly Added", books = newlyAddedBooks, sectionKey = "newly_added") }
            item { Section(title = "Free Books", books = freeBooks, sectionKey = "free_books") }
            item { Section(title = "Collections", books = collections, sectionKey = "collections") }
            item { Section(title = "Popular Books", books = popularBooks, sectionKey = "popular_books") }
            item { Section(title = "Top Rated Books", books = topRatedBooks, sectionKey = "top_rated_books") }

            // Random Feeds Section
            item { RandomFeedsSection(feeds = randomFeeds) }
        }
    }
}

@Composable
fun BannerSection(bannerImageUrl: String) {
    val context = LocalContext.current
    AsyncImage(
        model = bannerImageUrl,
        contentDescription = "Banner Image",
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable {
//                val intent = Intent(context, BookDetailsActivity::class.java)
//                context.startActivity(intent)
            },
        contentScale = ContentScale.Crop
    )
}

@Composable
fun RandomFeedsSection(feeds: List<FeedItem>) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = "Random Feeds",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF000000),
            modifier = Modifier.padding(start = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // LazyColumn for vertical feeds
        LazyColumn(
            modifier = Modifier.heightIn(max = 500.dp),
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(feeds) { feed ->
                FeedItemView(feed = feed)
            }
        }
    }
}

@Composable
fun FeedItemView(feed: FeedItem) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clickable {
                val intent = Intent(context, BookDetailsActivity::class.java).apply {
                    putExtra("BOOK_ID", feed.id) // pass data if needed
                }
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = feed.imageUrl,
                contentDescription = feed.title,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = feed.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF000000)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "More details about this feed...",
                    fontSize = 12.sp,
                    color = Color(0xFF878787)
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "View Feed",
                modifier = Modifier.size(35.dp).padding(start = 8.dp),
                tint = Color(0xFF463FA6)
            )
        }
    }
}

@Composable
fun Section(title: String, books: List<BookItem>, sectionKey: String) {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF000000),
                modifier = Modifier.padding(start = 4.dp)
            )
            Text(
                text = "See More",
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable {
                        val intent = Intent(context, BookListActivity::class.java)
                        intent.putExtra("PARAMETER", title)
                        intent.putExtra("SECTION_KEY", sectionKey) // Pass the section key
                        context.startActivity(intent)
                    }
                    .padding(end = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(books) { book ->
                BookItemView(book = book)
            }
        }
    }
}

@Composable
fun BookItemView(book: BookItem) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable {
                val intent = Intent(context, BookDetailsActivity::class.java).apply {
                    putExtra("BOOK_ID", book.id) // pass data if needed
                }
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = book.imageUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 4.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = book.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF000000),
                modifier = Modifier.padding(bottom = 2.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

