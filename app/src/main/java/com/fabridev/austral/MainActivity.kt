package com.fabridev.austral

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.fabridev.austral.data.local.AppDatabase
import com.fabridev.austral.data.local.UserPreferences // Asegurate de que este import esté bien
import com.fabridev.austral.ui.AppNavigation
import com.fabridev.austral.ui.screens.home.HomeViewModel
import com.fabridev.austral.ui.screens.home.HomeViewModelFactory
import com.fabridev.austral.ui.theme.AustralTheme
import java.util.concurrent.Executor

class MainActivity : FragmentActivity() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Configuración de Base de Datos
        val database = AppDatabase.getDatabase(this)
        val transactionDao = database.transactionDao()
        val goalDao = database.goalDao()
        val debtDao = database.debtDao()

        // 2. Preferencias (DataStore)
        val userPreferences = UserPreferences(this)

        // 3. Fábrica y ViewModel
        val viewModelFactory = HomeViewModelFactory(transactionDao, goalDao, debtDao, userPreferences)
        val viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        // 4. Configurar Seguridad (Huella + PIN)
        setupBiometrics(viewModel)

        // 5. Intentar entrar
        checkAndAuthenticate(viewModel)
    }

    private fun setupBiometrics(viewModel: HomeViewModel) {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                // Si hay error o el usuario cancela el PIN
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    // Si cancela voluntariamente o aprieta atrás, cerramos la app.
                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                        errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        finish()
                    }
                    // Nota: No mostramos Toast de error si cancela, para no molestar.
                }

                // ¡Éxito! (Ya sea por Huella o por PIN)
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext, "Bienvenido a Austral 🚀", Toast.LENGTH_SHORT).show()
                    showAppContent(viewModel)
                }

                // Falló la huella (pero puede seguir intentando)
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "No reconocido. Intentá de nuevo.", Toast.LENGTH_SHORT).show()
                }
            })

        // CONFIGURACIÓN CLAVE PARA PIN/PATRÓN
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Austral App 🔒")
            .setSubtitle("Usá tu Huella o PIN para entrar")
            // IMPORTANTE: Al activar DEVICE_CREDENTIAL, borramos el setNegativeButtonText
            // Esto permite que aparezca la opción de "Usar patrón/PIN"
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()
    }

    private fun checkAndAuthenticate(viewModel: HomeViewModel) {
        val biometricManager = BiometricManager.from(this)

        // Definimos qué tipos de seguridad aceptamos (Huella O Pin)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL

        when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Todo OK, lanzamos el prompt
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Si el celular no tiene seguridad configurada, dejamos pasar (Modo Dev)
                Toast.makeText(this, "Sin seguridad configurada, entrando...", Toast.LENGTH_SHORT).show()
                showAppContent(viewModel)
            }
            else -> {
                // En cualquier otro caso raro, intentamos mostrar la app
                showAppContent(viewModel)
            }
        }
    }

    private fun showAppContent(viewModel: HomeViewModel) {
        setContent {
            AustralTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}