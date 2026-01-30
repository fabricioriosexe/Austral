package com.fabridev.austral

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity // <--- IMPORTANTE: Usamos FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.fabridev.austral.data.local.AppDatabase
import com.fabridev.austral.ui.AppNavigation
import com.fabridev.austral.ui.screens.home.HomeViewModel
import com.fabridev.austral.ui.screens.home.HomeViewModelFactory
import com.fabridev.austral.ui.theme.AustralTheme
import java.util.concurrent.Executor

// Cambiamos de ComponentActivity a FragmentActivity para soportar el diálogo de huella
class MainActivity : FragmentActivity() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. CONFIGURACIÓN DEL VIEWMODEL (Igual que antes)
        val database = AppDatabase.getDatabase(this)
        val transactionDao = database.transactionDao()
        val goalDao = database.goalDao()
        val debtDao = database.debtDao()

        val viewModelFactory = HomeViewModelFactory(transactionDao, goalDao, debtDao)
        val viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        // 2. PREPARAR LA BIOMETRÍA
        setupBiometrics(viewModel)

        // 3. INTENTAR AUTENTICAR AL ABRIR
        // La app no muestra contenido (setContent) hasta que esto pase
        checkAndAuthenticate(viewModel)
    }

    private fun setupBiometrics(viewModel: HomeViewModel) {
        executor = ContextCompat.getMainExecutor(this)

        // Configurar qué pasa cuando el usuario pone el dedo
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                // CASO 1: ERROR (Dedo incorrecto muchas veces o canceló)
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Si el usuario cancela voluntariamente o hay error grave, cerramos la app
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON || errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                        finish() // Cierra la app. ¡Sin huella no hay paraíso!
                    }
                    Toast.makeText(applicationContext, "Error: $errString", Toast.LENGTH_SHORT).show()
                }

                // CASO 2: ÉXITO (Dedo correcto)
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // ¡BARRERA LEVANTADA! Mostramos la app
                    Toast.makeText(applicationContext, "Identidad verificada 🚀", Toast.LENGTH_SHORT).show()
                    showAppContent(viewModel)
                }

                // CASO 3: FALLO TEMPORAL (Puso mal el dedo, puede intentar de nuevo)
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Huella no reconocida", Toast.LENGTH_SHORT).show()
                }
            })

        // Configurar cómo se ve el cartelito
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Austral App 🔒")
            .setSubtitle("Escaneá tu huella para ver tus finanzas")
            .setNegativeButtonText("Salir") // Botón para cancelar
            .build()
    }

    private fun checkAndAuthenticate(viewModel: HomeViewModel) {
        val biometricManager = BiometricManager.from(this)

        // Verificamos si el celular TIENE capacidad biométrica
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // SÍ TIENE -> Lanzamos el diálogo
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // NO TIENE (o no configuró huella) -> Dejamos pasar (Modo seguro desactivado)
                // Esto es importante para el Emulador si no configuraste huella
                Toast.makeText(this, "Sin seguridad biométrica, entrando...", Toast.LENGTH_SHORT).show()
                showAppContent(viewModel)
            }
            else -> {
                showAppContent(viewModel)
            }
        }
    }

    // Función auxiliar para cargar la pantalla de Compose
    private fun showAppContent(viewModel: HomeViewModel) {
        setContent {
            AustralTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}