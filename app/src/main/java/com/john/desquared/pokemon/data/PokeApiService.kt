package com.john.desquared.pokemon.data

import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiService {
    @GET("type/{type}")
    suspend fun getPokemonByType(
        @Path("type") type: String
    ): TypeResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonDetails(
        @Path("name") name: String
    ): PokemonDetailResponse
}