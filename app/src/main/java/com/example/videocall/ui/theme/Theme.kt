package com.example.videocall.ui.theme

import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Black80,            // dark color for primary
    secondary = DarkGrey80,       // dark grey for secondary
    tertiary = LightGrey80,       // light grey for tertiary

    background = Color(0xFF121212),  // dark background
    surface = Color(0xFF1E1E1E),     // dark surface color
    onPrimary = Color.White,          // text/icons on primary
    onSecondary = Color.White,        // text/icons on secondary
    onTertiary = Color.Black,         // text/icons on tertiary
    onBackground = Color.White,       // text/icons on background
    onSurface = Color.White           // text/icons on surface
)

private val LightColorScheme = lightColorScheme(
    primary = Black40,           // dark grey for primary in light theme
    secondary = DarkGrey40,      // darker grey for secondary
    tertiary = LightGrey40,      // light grey for tertiary

    background = Color(0xFFFFFFFF), // light background
    surface = Color(0xFFF2F2F2),    // light surface color
    onPrimary = Color.Black,        // text/icons on primary
    onSecondary = Color.Black,      // text/icons on secondary
    onTertiary = Color.Black,       // text/icons on tertiary
    onBackground = Color.Black,     // text/icons on background
    onSurface = Color.Black         // text/icons on surface
)

@Composable
fun VideocallTheme(
    darkTheme: Boolean = true,    // Force dark theme by default
    dynamicColor: Boolean = false, // Disable dynamic color for consistency
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
