package com.application.sarmyatnhar.ui.components

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.sarmyatnhar.ui.activities.CategoryActivity
import com.application.sarmyatnhar.ui.activities.HomeActivity
import com.application.sarmyatnhar.ui.activities.LibraryActivity
import com.application.sarmyatnhar.ui.activities.ProfileActivity
import com.application.sarmyatnhar.ui.activities.WriterActivity
import com.application.sarmyatnhar.ui.theme.primaryColor
import com.application.sarmyatnhar.ui.theme.secondaryColor

/**
 * Data class to represent a single navigation item.
 */
sealed class BottomNavItem(val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("Home", Icons.Default.Home)
    object Categories : BottomNavItem("Categories", Icons.Default.Category)
    object Saved : BottomNavItem("Library", Icons.Default.LocalLibrary)
    object Pen : BottomNavItem("Write", Icons.Default.Create)
    object Profile : BottomNavItem("Profile", Icons.Default.Person)
}

/**
 * A reusable composable for the app's bottom navigation bar.
 *
 * @param selectedItem The title of the currently selected item.
 * @param onItemSelected A lambda function to be called when an item is clicked,
 * which passes the title of the clicked item.
 */
@Composable
fun BottomNavigationBar(selectedItem: String, onItemSelected: (String) -> Unit) {
    val context = LocalContext.current
    val currentActivity = context as? Activity
    val currentActivityName = currentActivity?.javaClass?.simpleName ?: ""

    // State to hold the destination class to launch
    val destinationState = remember { mutableStateOf<Class<*>?>(null) }

    // Side-effect to launch activity when destinationState changes
    LaunchedEffect(destinationState.value) {
        destinationState.value?.let { destination ->
            val intent = Intent(context, destination)
            context.startActivity(intent)
            val activity = context as? Activity
            activity?.finish()
            destinationState.value = null
        }
    }

    BottomAppBar(
        containerColor = Color(0xFFF0F5FF), // Lighter blue for the bar
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        val navItems = listOf(
            BottomNavItem.Home,
            BottomNavItem.Categories,
            BottomNavItem.Saved,
            BottomNavItem.Pen,
            BottomNavItem.Profile
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            // Update the destination state on click
                            val destination: Class<*> = when (item.title) {
                                "Home" -> HomeActivity::class.java
                                "Categories" -> CategoryActivity::class.java
                                "Library" -> LibraryActivity::class.java
                                "Write" -> WriterActivity::class.java
                                "Profile" -> ProfileActivity::class.java
                                else -> HomeActivity::class.java
                            }
                            destinationState.value = destination
                            onItemSelected(item.title)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val isSelected = when (currentActivityName) {
                        "HomeActivity" -> item.title == "Home"
                        "CategoryActivity" -> item.title == "Categories"
                        "LibraryActivity" -> item.title == "Library"
                        "WriterActivity" -> item.title == "Write"
                        "ProfileActivity" -> item.title == "Profile"
                        else -> false
                    }
                    val iconTint = if (isSelected) primaryColor else secondaryColor

                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = item.title,
                        fontSize = 10.sp,
                        color = primaryColor
                    )
                }
            }
        }
    }
}
