package com.john.desquared.pokemon.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.john.desquared.pokemon.data.RetrofitClient
import com.john.desquared.pokemon.model.Pokemon
import com.john.desquared.pokemon.repository.PokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
class PokemonViewModel : ViewModel() {
    private val repository = PokemonRepository()
    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    private val _selectedType = MutableStateFlow("fire")
    private val _isLoading = MutableStateFlow(false)
    private val _searchText = MutableStateFlow("")
    private val _showNotFoundError = MutableStateFlow(false)
    private val _filteredPokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    private var isFetchingMore = false
    private var currentOffset = 0
    private var searchJob: Job? = null
    val filteredPokemonList: StateFlow<List<Pokemon>> = _filteredPokemonList
    val selectedType: StateFlow<String> = _selectedType
    val isLoading: StateFlow<Boolean> = _isLoading
    val searchText: StateFlow<String> = _searchText
    val showNotFoundError: StateFlow<Boolean> = _showNotFoundError

    init {
        fetchPokemon("fire")
    }

    /**
     * Updates the search query and manages filtering logic.
     * If no local matches are found for a query of 3+ characters,
     * it triggers a global API search as a fallback.
     */
    fun updateSearchText(newText: String) {
        _searchText.value = newText

        val localResults = _pokemonList.value.filter {
            it.name.contains(newText, ignoreCase = true)
        }
        _filteredPokemonList.value = localResults

        searchJob?.cancel()

        if (newText.length >= 3 && localResults.isEmpty()) {
            _showNotFoundError.value = false

            searchJob = viewModelScope.launch {
                try {
                    val details = RetrofitClient.apiService.getPokemonDetails(newText.lowercase())
                    val primaryType = details.types.firstOrNull()?.type?.name ?: "normal"
                    _selectedType.value = primaryType

                    var fetchedHp = 0
                    var fetchedAttack = 0
                    var fetchedDefense = 0

                    details.stats.forEach { statEntry ->
                        when (statEntry.stat.name) {
                            "hp" -> fetchedHp = statEntry.base_stat
                            "attack" -> fetchedAttack = statEntry.base_stat
                            "defense" -> fetchedDefense = statEntry.base_stat
                        }
                    }

                    val isolatedPokemon = Pokemon(
                        id = details.id,
                        name = details.name,
                        type = primaryType,
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${details.id}.png",
                        hp = fetchedHp,
                        attack = fetchedAttack,
                        defense = fetchedDefense
                    )

                    _filteredPokemonList.value = listOf(isolatedPokemon)

                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("PokemonViewModel", "Global search failed", e)
                    _filteredPokemonList.value = emptyList()
                    _showNotFoundError.value = true
                }
            }
        } else if (localResults.isNotEmpty()) {
            _showNotFoundError.value = false
        }
    }

    /**
     * Resets the list and fetches the first 10 Pokémon for a specific category (e.g., "Fire").
     * Clears the previous state and shows the loading spinner.
     */
    fun fetchPokemon(typeName: String) {
        _selectedType.value = typeName
        currentOffset = 0

        viewModelScope.launch {
            _isLoading.value = true
            val newTeam = repository.getPokemonTeam(typeName, currentOffset)
            _pokemonList.value = newTeam
            _filteredPokemonList.value = newTeam
            currentOffset += 10
            _isLoading.value = false
        }
    }

    /**
     * Fetches the next 10 Pokémon without clearing the existing list.
     * Prevents duplicate requests using 'isFetchingMore' and appends data to the current list.
     */
    fun loadMore() {
        if (_isLoading.value || isFetchingMore) return

        isFetchingMore = true
        viewModelScope.launch {
            val nextBatch = repository.getPokemonTeam(_selectedType.value, currentOffset)
            if (nextBatch.isNotEmpty()) {
                _pokemonList.value += nextBatch

                if (_searchText.value.isEmpty()) {
                    _filteredPokemonList.value += nextBatch
                }

                currentOffset += 10
            }
            isFetchingMore = false
        }
    }
}