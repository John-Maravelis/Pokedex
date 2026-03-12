package com.john.desquared.pokemon.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.john.desquared.pokemon.model.Pokemon
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * A horizontal, clickable button used to filter Pokémon by their type.
 * Changes color dynamically based on whether it is currently selected.
 */
@Composable
fun TypeSelectorPill(
    typeName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) getTypeColor(typeName) else Color(0xFFE0E0E0)
    val textColor = if (isSelected) Color.White else Color.DarkGray

    Surface(
        modifier = Modifier
            .padding(end = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = typeName.replaceFirstChar { it.uppercase() },
            color = textColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}


/**
 * The main container that displays Pokémon in a 2-column grid.
 * Handles "Load More" pagination and manages which card is currently expanded.
 */
@Composable
fun PokemonGrid(
    pokemonList: List<Pokemon>,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedPokemonId by remember { mutableStateOf<Int?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
    ) {
        itemsIndexed(pokemonList) { index, currentPokemon ->
            PokemonCard(
                pokemon = currentPokemon,
                isExpanded = currentPokemon.id == expandedPokemonId,
                onCardClick = {
                    expandedPokemonId = if (expandedPokemonId == currentPokemon.id) null else currentPokemon.id
                }
            )

            if (index == pokemonList.lastIndex) {
                LaunchedEffect(index) {
                    onLoadMore()
                }
            }
        }
    }
}

/**
 * An interactive card representing a single Pokémon.
 * Displays the sprite, ID, and name, and expands to show stats when clicked.
 */
@Composable
fun PokemonCard(
    pokemon: Pokemon,
    isExpanded: Boolean,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = "${pokemon.name} sprite",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "#${pokemon.id}",
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = pokemon.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Surface(
                color = getTypeColor(pokemon.type),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = pokemon.type,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                ) {
                    StatBar(statName = "HP", statValue = pokemon.hp, color = Color(0xFF4CAF50))
                    StatBar(statName = "ATK", statValue = pokemon.attack, color = Color(0xFFFF4B4B))
                    StatBar(statName = "DEF", statValue = pokemon.defense, color = Color(0xFF4A90E2))
                }
            }
        }
    }
}

/**
 * A styled text input field that allows users to filter the Pokémon list by name.
 * Features a rounded design and a leading search icon.
 */
@Composable
fun PokemonSearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchText,

        onValueChange = onSearchTextChanged,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Search Pokemon...") },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon") },
        shape = RoundedCornerShape(24.dp),
        singleLine = true
    )
}

/**
 * A horizontal row that displays a stat name, a visual progress bar,
 * and the numerical value for Pokémon attributes (HP, ATK, DEF).
 */
@Composable
fun StatBar(statName: String, statValue: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text(
            text = statName,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.width(40.dp)
        )

        LinearProgressIndicator(
            progress = { statValue / 150f },
            modifier = Modifier.weight(1f).height(8.dp),
            color = color,
            trackColor = Color.LightGray
        )

        Text(
            text = statValue.toString(),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

/**
* A simple informational view displayed when a search yields no results locally or globally.
* Informs the user that the specific Pokémon could not be found in the database.
*/
@Composable
fun PokemonNotFoundView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "❌", fontSize = 48.sp)
        Text(
            text = "No Pokémon Found",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "We couldn't find a match for your search.",
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

/**
 * Maps a Pokémon type string to a specific Material Design color for the UI.
 */
fun getTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "fire" -> Color(0xFFFF4B4B)
        "water" -> Color(0xFF4A90E2)
        "grass" -> Color(0xFF4CAF50)
        "electric" -> Color(0xFFFFC107)
        "psychic" -> Color(0xFF9C27B0)
        "normal" -> Color(0xFF9E9E9E)
        "fighting" -> Color(0xFFFF9800)
        else -> Color(0xFF607D8B)
    }
}