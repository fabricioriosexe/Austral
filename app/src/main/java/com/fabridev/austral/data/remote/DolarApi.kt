package com.fabridev.austral.data.remote

import retrofit2.http.GET

interface DolarApi {
    // Le pegamos a la URL: https://dolarapi.com/v1/dolares/blue
    @GET("v1/dolares/blue")
    suspend fun getDolarBlue(): DolarDto
}