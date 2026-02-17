<div align="center">
  <img src="captura%20de%20la%20app/logo.png" alt="Logo Austral" width="140">
  <h1>🇦🇷 Austral</h1>

  <p>
    <strong>Tu escudo financiero contra la inflación.</strong>
  </p>

  <p>
     <img src="https://img.shields.io/badge/Kotlin-1.9.x-purple?logo=kotlin" alt="Kotlin">
    <img src="https://img.shields.io/badge/Jetpack%20Compose-Material3-4285F4?logo=android" alt="Jetpack Compose">
    <img src="https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-green" alt="Architecture">
    <img src="https://img.shields.io/badge/DB-Room-lightgrey?logo=sqlite" alt="Room Database">
  </p>
</div>

---

## 💡 Sobre el Proyecto

**Austral** es una billetera inteligente diseñada específicamente para navegar la economía argentina. Permite a los usuarios gestionar sus finanzas personales en un entorno multi-moneda (ARS, USD), con el objetivo de proteger el poder adquisitivo del usuario frente a la inflación.

El proyecto combina una interfaz de usuario moderna y fluida con herramientas prácticas para el seguimiento de gastos, el establecimiento de metas de ahorro y la visualización del patrimonio real en moneda fuerte.

## 📱 Galería

Una vista rápida de las funciones principales de Austral:

| Pantalla de Carga | Home Dashboard | Metas de Ahorro |
|:---:|:---:|:---:|
| <img src="captura%20de%20la%20app/Splash.png" width="200" alt="Splash Screen"> | <img src="captura%20de%20la%20app/home1.png" width="200" alt="Home Screen"> | <img src="captura%20de%20la%20app/metas.png" width="200" alt="MetasScreen"> |
| **Registro de Ingresos** | **Registro de Gastos** | **Control de Deudas** |
| <img src="captura%20de%20la%20app/p%20Ingresos.png" width="200" alt="Pantalla Ingresos"> | <img src="captura%20de%20la%20app/p%20Gastos.png" width="200" alt="Pantalla Gastos"> | <img src="captura%20de%20la%20app/p%20Deudas%201.png" width="200" alt="Pantalla Deudas"> |

## ✨ Funcionalidades Principales

Estas son las características que ya están implementadas y funcionando en la versión actual:

### 💵 Economía y Moneda
- [x] **Cotización en Tiempo Real:** Integración con API para consultar y visualizar el valor actualizado del Dólar Blue y otras cotizaciones financieras al instante.

### 📊 Gestión Financiera (Basado en tus capturas)
- [x] **Registro de Transacciones:** Módulo completo para registrar Ingresos y Gastos de forma intuitiva.
- [x] **Metas de Ahorro:** Herramientas para establecer objetivos financieros específicos y visualizar el progreso hacia ellos.
- [x] **Control de Deudas:** Sección dedicada para llevar un seguimiento ordenado de préstamos o deudas pendientes.

### ⚙️ Técnica y UX
- [x] **Persistencia Local Robusta:** Uso de Room Database (SQLite) para un almacenamiento seguro y rápido de los datos en el dispositivo.
- [x] **Diseño Moderno (Material 3):** Interfaz de usuario construida 100% con Jetpack Compose, ofreciendo una experiencia fluida y animaciones limpias.
- [x] **Modo Oscuro:** Soporte nativo para Dark Mode/Light Mode, adaptándose a la preferencia del sistema del usuario.
- [x] **Arquitectura Limpia:** Separación estricta de responsabilidades mediante MVVM y Clean Architecture para un código mantenible y testeable.

## 🚀 Stack Tecnológico

Este proyecto utiliza las últimas prácticas de desarrollo Android moderno:

* **Lenguaje:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material Design 3)
* **Arquitectura:** MVVM + Clean Architecture
* **Inyección de Dependencias:** Hilt
* **Base de Datos:** Room con KSP
* **Red (Networking):** Retrofit + Coroutines/Flow (para la API del Dólar)
* **Navegación:** Jetpack Navigation Compose

## 🗺️ Roadmap (Próximamente)

Estas funcionalidades están planificadas para futuras actualizaciones:

- [ ] 🤖 **Smart Scanner (IA):** Integración con Google ML Kit para escanear tickets físicos y automatizar la carga de gastos.
- [ ] 🎮 **Sistema de Logros (Gamificación):** Implementación de recompensas visuales y niveles para motivar el hábito del ahorro.

## 🛠️ Configuración e Instalación

1.  Clonar el repositorio:
    ```bash
    git clone [https://github.com/fabricioriosexe/Austral.git](https://github.com/fabricioriosexe/Austral.git)
    ```
2.  Abrir el proyecto en **Android Studio** (Recomendado: versión Koala o superior).
3.  Esperar a que Gradle sincronice las dependencias.
4.  Ejecutar en un emulador o dispositivo físico (Min SDK 26 / Android 8.0).

---

<div align="center">
  <p>Desarrollado con 🧉 en Argentina</p>
  **Fabricio Exequiel Rios**<br>
  Desarrollador Android & Innovación Tecnológica
</div>
