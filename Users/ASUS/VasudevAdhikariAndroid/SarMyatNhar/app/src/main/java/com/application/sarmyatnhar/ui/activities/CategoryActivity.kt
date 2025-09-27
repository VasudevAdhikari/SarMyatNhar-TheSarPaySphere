package com.application.sarmyatnhar.ui.activities

import android.R.color.transparent
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.application.sarmyatnhar.ui.components.BottomNavigationBar
import com.application.sarmyatnhar.ui.theme.SarMyatNharTheme
import com.application.sarmyatnhar.ui.theme.gradientBrush
import com.application.sarmyatnhar.ui.theme.primaryColor
import com.application.sarmyatnhar.ui.theme.whiteColor

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SarMyatNharTheme {
                CategoryScreen()
            }
        }
    }
}

@Composable
fun CategoryScreen() {
    val context = LocalContext.current
    var selectedItem by remember { mutableStateOf("Home") }
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
                    modifier = Modifier.size(24.dp)
                        .clickable {
                            activity?.finish()
                        }
                )
                Spacer(modifier = Modifier.width(16.dp))
                PoppinsText(
                    text = "Categories",
                    color = primaryColor,
                    fontSize = 20,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Category Grid
            val allCategories = listOf(
                "Fiction", "NonFiction", "SciFi", "Fantasy", "Mystery", "Thriller",
                "Romance", "History", "Poetry", "Drama", "Comics", "Manga",
                "Children", "YoungAdult", "Biography", "Memoir", "SelfHelp", "Philosophy",
                "Psychology", "Religion", "Politics", "Science", "Math", "Medical",
                "Business", "Education", "Travel", "Cooking", "Art", "Law"
            )

            val categories = allCategories.map { category ->
                if (category.length > 8) category.substring(0, 8) else category
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Use weight to fill the remaining space
                    .clip(RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    CategoryItem(category = category)
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: String) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(primaryColor)
            .padding(16.dp)
            .clickable {
                val intent = Intent(context, BookListActivity::class.java)
                intent.putExtra("PARAMETER", category)
                context.startActivity(intent)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = "Book Icon",
            tint = whiteColor,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        PoppinsText(
            text = category,
            color = whiteColor,
            fontSize = 14
        )
    }
}

