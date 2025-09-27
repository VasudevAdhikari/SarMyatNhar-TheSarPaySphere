package com.application.sarmyatnhar.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)


val primaryColor = Color(0xFF4741A6) // 70%
val secondaryColor = Color(0xFF9BBBFC) // 20%
val accentColor = Color(0xFFD9EFF7) // 10%

val goldenColor = Color(0xFFEEB700)
val silverColor = Color(0xFFB5B5B5)

val whiteColor = Color(0xFFFFFFFF)
val blackColor = Color(0xFF000000)

val gradientBrush = Brush.linearGradient(
    colors = listOf(
        Color.White,
        accentColor,
        Color.White
    ),
    start = Offset.Zero, // top-left corner
    end = Offset.Infinite // bottom-right corner, equivalent to 135 degrees
)