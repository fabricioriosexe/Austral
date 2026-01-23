package com.fabridev.austral.data.remote

import com.google.gson.annotations.SerializedName

// DTO = Data Transfer Object. Es la representación exacta del JSON.
data class DolarDto(
    @SerializedName("compra") val compra: Double,
    @SerializedName("venta") val venta: Double,
    @SerializedName("fechaActualizacion") val fechaActualizacion: String
)