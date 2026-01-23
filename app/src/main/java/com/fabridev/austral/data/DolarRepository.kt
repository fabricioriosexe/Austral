package com.fabridev.austral.data

import android.util.Log
import com.fabridev.austral.data.remote.DolarApi

class DolarRepository(private val api: DolarApi) {

    // Devuelve el valor de VENTA (que es el que nos importa para saber cuánto tenemos)
    // Si falla, devuelve null para no romper la app.
    suspend fun getBluePrice(): Double? {
        return try {
            val response = api.getDolarBlue()
            Log.d("AustralAPI", "Dolar Blue: ${response.venta}")
            response.venta
        } catch (e: Exception) {
            Log.e("AustralAPI", "Error buscando dolar: ${e.message}")
            null // Si no hay internet o falla, devolvemos null
        }
    }
}