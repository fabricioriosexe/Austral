package com.fabridev.austral.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Definimos que el esquema de colores sea OSCURO
private val DarkColorScheme = darkColorScheme(
    primary = AustralPrimary,
    secondary = AustralSecondary,
    background = AustralBackground,
    surface = AustralSurface,
    error = AustralError,
    onPrimary = TextWhite,
    onSecondary = AustralBackground,
    onBackground = TextWhite,
    onSurface = TextWhite
)

@Composable
fun AustralTheme(
    content: @Composable () -> Unit
) {
    // Forzamos el esquema oscuro
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Pintamos la barra de estado (donde está la hora y batería) del color de fondo
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Usa la tipografía por defecto (o la que esté en Type.kt)
        content = content
    )
}