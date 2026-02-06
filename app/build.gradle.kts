plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // ESTO ES CLAVE: Activamos el procesador para la base de datos
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.fabridev.austral"
    compileSdk = 35 // Usamos 35 (Android 15) que es estable. 36 es beta.

    defaultConfig {
        applicationId = "com.fabridev.austral"
        minSdk = 26 // Subimos a 26 (Android 8) para tener mejores fechas y seguridad
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- 1. UI: Jetpack Compose (Interfaz Moderna) ---
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    // Iconos extra (para el carrito, wallet, flechas, etc)
    implementation("androidx.compose.material:material-icons-extended")

    // --- 2. Navegación ---
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // --- 3. Datos Locales: Room (Base de Datos) ---
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    // El procesador mágico que crea el código SQL por vos:
    ksp("androidx.room:room-compiler:$roomVersion")

    // --- 4. Conexión a Internet: Retrofit (Dolar API) ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // --- 5. Arquitectura: ViewModel ---
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // --- 6. Herramientas de Debug y Test ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Biometría
    implementation("androidx.biometric:biometric:1.1.0")
    //datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}