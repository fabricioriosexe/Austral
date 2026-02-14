package com.fabridev.austral.ui.utils

import java.text.NumberFormat
import java.util.Locale

fun Double.toCurrency(currencyCode: String = "ARS"): String {
    val locale = if (currencyCode == "USD") Locale.US else Locale("es", "AR")
    val format = NumberFormat.getCurrencyInstance(locale)

    // Hack para que ARS no muestre el símbolo "ARS" sino "$"
    if (currencyCode == "ARS") {
        format.currency = java.util.Currency.getInstance("ARS")
        // A veces Java pone "ARS 100", forzamos el símbolo si es necesario,
        // pero generalmente el locale es_AR usa $.
    }

    return format.format(this)
}

// Extension para formatear porcentajes
fun Double.toPercent(): String {
    return (if (this >= 0) "+" else "") + String.format("%.2f%%", this)
}