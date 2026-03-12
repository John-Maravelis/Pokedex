package com.john.desquared.pokemon.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * RetrofitClient: The central hub for all network communication.
 * * This is Singleton that configures how the app talks to the PokeAPI.
 * It uses 'lazy' initialization to save memory by only building the
 * connection the moment it's actually needed. It also includes
 * a GSON translator to automatically turn JSON text into Kotlin objects.
 */
object RetrofitClient {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"
    val apiService: PokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApiService::class.java)
    }
}