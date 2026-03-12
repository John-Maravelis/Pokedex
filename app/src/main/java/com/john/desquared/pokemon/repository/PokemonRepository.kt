package com.john.desquared.pokemon.repository

import android.util.Log
import com.john.desquared.pokemon.data.RetrofitClient
import com.john.desquared.pokemon.model.Pokemon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * PokemonRepository: This class handles the complex logic of communicating with the PokéAPI.
 * Its primary responsibilities include:
 * 1. Performing network requests on background threads using Dispatchers.IO.
 * 2. Implementing Pagination (offset/limit) to fetch data in small, efficient batches.
 * 3. Mapping raw JSON responses (PokemonDetailResponse) into clean UI models (Pokémon).
 * 4. Error handling to ensure the app remains stable if the internet connection fails.
 */
class PokemonRepository {
    suspend fun getPokemonTeam(typeName: String, offset: Int): List<Pokemon> {
        return withContext(Dispatchers.IO) {
            try {
                val typeResponse = RetrofitClient.apiService.getPokemonByType(typeName.lowercase())
                val nextBatchOfNames = typeResponse.pokemon.drop(offset).take(10)
                val finalTeam = nextBatchOfNames.map { item ->
                    val details = RetrofitClient.apiService.getPokemonDetails(item.pokemon.name)
                    val hpStat = details.stats.find { it.stat.name == "hp" }?.base_stat ?: 0
                    val atkStat = details.stats.find { it.stat.name == "attack" }?.base_stat ?: 0
                    val defStat = details.stats.find { it.stat.name == "defense" }?.base_stat ?: 0
                    Pokemon(
                        id = details.id,
                        name = details.name.replaceFirstChar { it.uppercase() },
                        type = typeName.replaceFirstChar { it.uppercase() },
                        imageUrl = details.sprites.front_default ?: "",
                        hp = hpStat,
                        attack = atkStat,
                        defense = defStat
                    )
                }
                finalTeam

            } catch (e: Exception) {
                Log.e("PokemonRepository", "Error fetching data: ${e.message}")
                emptyList()
            }
        }
    }
}