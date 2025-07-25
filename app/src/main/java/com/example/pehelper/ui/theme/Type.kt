package com.example.pehelper.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.pehelper.R

val Mulish = FontFamily(
    Font(R.font.mulish_regular),
    Font(R.font.mulish_light),
    Font(R.font.mulish_extrabold),
    Font(R.font.mulish_bold),
    Font(R.font.mulish_semibold),
    Font(R.font.mulish_medium)
)

// Set of Material typography styles to start with
val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)