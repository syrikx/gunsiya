package com.hyunakim.gunsiya.ui.theme


import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

//private val EczarFontFamily = FontFamily(
//    Font(R.font.eczar_regular),
//    Font(R.font.eczar_semibold, FontWeight.SemiBold)
//)
//private val RobotoCondensed = FontFamily(
//    Font(R.font.robotocondensed_regular),
//    Font(R.font.robotocondensed_light, FontWeight.Light),
//    Font(R.font.robotocondensed_bold, FontWeight.Bold)
//)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)