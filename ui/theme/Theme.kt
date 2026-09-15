package com.prepcommerce.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(primary = androidx.compose.ui.graphics.Color(0xFF1565C0))
private val DarkColors = darkColorScheme(primary = androidx.compose.ui.graphics.Color(0xFF90CAF9))

@Composable
fun PrepCommerceTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}
