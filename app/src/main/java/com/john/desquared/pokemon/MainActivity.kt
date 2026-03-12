package com.john.desquared.pokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.john.desquared.pokemon.ui.theme.PokemonTheme
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.john.desquared.pokemon.ui.component.PokemonGrid
import com.john.desquared.pokemon.ui.component.PokemonSearchBar
import com.john.desquared.pokemon.ui.component.TypeSelectorPill
import com.john.desquared.pokemon.ui.component.PokemonNotFoundView
import com.john.desquared.pokemon.viewmodel.PokemonViewModel
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PokemonTheme {
                // Initialize ViewModel tied to the activity lifecycle
                val viewModel: PokemonViewModel = viewModel()

                // Observe UI states from the ViewModel
                val displayList by viewModel.filteredPokemonList.collectAsState()
                val showNotFound by viewModel.showNotFoundError.collectAsState()
                val currentType by viewModel.selectedType.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()
                val searchText by viewModel.searchText.collectAsState()
                val pokemonTypes = listOf("fire", "water", "grass", "electric", "psychic", "normal", "fighting")

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .background(Color(0xFFF8F9FA))
                    ) {
                        Text(
                            text = "Welcome Trainer!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )
                        PokemonSearchBar(
                            searchText = searchText,
                            onSearchTextChanged = { newText -> viewModel.updateSearchText(newText) }
                        )
                        LazyRow(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(pokemonTypes) { type ->
                                TypeSelectorPill(
                                    typeName = type,
                                    isSelected = type == currentType,
                                    onClick = { viewModel.fetchPokemon(type) }
                                )
                            }
                        }
                        // UI State Machine: Determines which view to render based on current data
                        if (isLoading && displayList.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFFFF4B4B))
                            }
                        } else if (displayList.isEmpty() && showNotFound && searchText.isNotEmpty()) {
                            PokemonNotFoundView()
                        } else {
                            PokemonGrid(
                                pokemonList = displayList,
                                onLoadMore = { viewModel.loadMore() }
                            )
                        }
                    }
                }
            }
        }
    }
}