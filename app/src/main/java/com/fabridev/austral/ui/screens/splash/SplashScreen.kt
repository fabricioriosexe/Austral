package com.fabridev.austral.ui.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabridev.austral.R // <--- IMPORTANTE: Asegúrate de que esto apunte a TU paquete
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }

    // Animación de opacidad (fade in) - Dura 1.5 segundos
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500), label = "fade"
    )

    // Efecto de navegación
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500) // Le di un poquito más (2.5s) para que se aprecie el logo
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF161B26)), // <--- Usamos tu color de fondo oscuro exacto
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alphaAnim.value)
        ) {
            // --- AQUÍ ESTÁ TU LOGO ---
            Image(
                // Asegúrate de que tu imagen se llame 'logo_austral' en res/drawable
                painter = painterResource(id = R.drawable.logo_austral_full),
                contentDescription = "Logo Austral",
                modifier = Modifier
                    .width(200.dp) // Ajusta este tamaño según lo grande que quieras el logo
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )


        }
    }
}