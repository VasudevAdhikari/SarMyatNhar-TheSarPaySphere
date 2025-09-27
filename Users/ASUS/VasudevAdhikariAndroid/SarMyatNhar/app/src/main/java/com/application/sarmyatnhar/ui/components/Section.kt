package com.application.sarmyatnhar.ui.components

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.application.sarmyatnhar.ui.activities.BookDetailsActivity
import com.application.sarmyatnhar.ui.activities.BookItem
import com.application.sarmyatnhar.ui.activities.BookListActivity
import com.application.sarmyatnhar.ui.theme.primaryColor

@Composable
fun Section(title: String, books: List<com.application.sarmyatnhar.ui.activities.BookItem>, sectionKey: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        SectionHeader(title = title, sectionKey = sectionKey)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(books) { book ->
                BookItemView(book = book)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, sectionKey: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF000000)
        )
        TextButton(
            onClick = {
                val intent = Intent(context, com.application.sarmyatnhar.ui.activities.BookListActivity::class.java).apply {
                    putExtra("PARAMETER", sectionKey)
                }
                context.startActivity(intent)
            },
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "See All",
                    fontSize = 16.sp,
                    color = Color(0xFF000000)
                )
                Spacer(modifier = Modifier.width(24.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "See All Arrow",
                    modifier = Modifier.size(30.dp),
                    tint = Color(0xFF463FA6)
                )
            }
        }
    }
}

@Composable
fun BookItemView(book: BookItem) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .clickable {
                val intent = Intent(context, BookDetailsActivity::class.java).apply {
                    putExtra("BOOK_ID", book.id)
                }
                context.startActivity(intent)
            }
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            AsyncImage(
                model = book.imageUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .width(100.dp)
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = book.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 4.dp),
            color = primaryColor
        )
    }
}
// Dummy data class for preview
data class DummyBookItem(val id: Int, val title: String, val imageUrl: String)

@Preview
@Composable
fun PreviewSection() {
    val dummyBooks = listOf(
        DummyBookItem(1, "Onyx Storm", "https://via.placeholder.com/150"),
        DummyBookItem(2, "The Dragon Reborn", "https://via.placeholder.com/150"),
        DummyBookItem(3, "The Name of the Wind", "https://via.placeholder.com/150")
    )
    Section(title = "Preview Section", books = dummyBooks.map {
        BookItem(it.id, it.title, it.imageUrl)
    }, sectionKey = "preview_section")
}
