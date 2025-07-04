// Typography.kt
package com.example.moderntodo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.moderntodo.R

// Modern font family using reliable system fonts
// Using sans-serif which provides a clean, modern look across all Android devices
val ModernFontFamily = FontFamily.SansSerif

// Alternative: Monospace for code-like elements (if needed)
val MonospaceFontFamily = FontFamily.Monospace

// Serif for formal text (if needed)
val SerifFontFamily = FontFamily.Serif

// You can uncomment and use Google Fonts if you prefer (requires internet connection):
// val GoogleFontFamily = FontFamily(
//     Font(R.font.inter_font, FontWeight.Normal),
//     Font(R.font.poppins_font, FontWeight.Medium)
// )

val Typography = Typography(
    // Display styles for hero text - more modern spacing
    displayLarge = TextStyle(
        fontFamily = ModernFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = ModernFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = ModernFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),

    // Headline styles for section headers - improved readability
    headlineLarge = TextStyle(
        fontFamily = ModernFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.2).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = ModernFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.1).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = ModernFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.1).sp
    ),

    // Title styles for card headers and important text - modern spacing
    titleLarge = TextStyle(
        fontFamily = ModernFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.05).sp
    ),
    titleMedium = TextStyle(
        fontFamily = ModernFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = ModernFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // Body styles for main content - optimized readability
    bodyLarge = TextStyle(
        fontFamily = ModernFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = ModernFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    ),
    bodySmall = TextStyle(
        fontFamily = ModernFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp
    ),

    // Label styles for UI elements - crisp and clear
    labelLarge = TextStyle(
        fontFamily = ModernFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = ModernFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontFamily = ModernFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
)

// Additional modern text styles for UI components
object ModernTextStyles {
    val cardTitle: TextStyle
        get() = TextStyle(
            fontFamily = ModernFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.1.sp
        )

    val cardSubtitle: TextStyle
        get() = TextStyle(
            fontFamily = ModernFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.2.sp
        )

    val buttonText: TextStyle
        get() = TextStyle(
            fontFamily = ModernFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.5.sp
        )

    val captionText: TextStyle
        get() = TextStyle(
            fontFamily = ModernFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.3.sp
        )

    val appBarTitle: TextStyle
        get() = TextStyle(
            fontFamily = ModernFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = (-0.1).sp
        )
}