package com.john.desquared.pokemon.data

data class TypeResponse(
    val pokemon: List<TypePokemon>
)

data class TypePokemon(
    val pokemon: NamedApiResource
)

data class NamedApiResource(
    val name: String,
    val url: String
)

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val sprites: Sprites,
    val stats: List<StatWrapper>,
    val types: List<TypeWrapper>
)

data class Sprites(
    // should I have used @SerializedName("front_default") ???
    val front_default: String?
)


data class StatWrapper(
    val base_stat: Int,
    val stat: NamedApiResource
)

data class TypeWrapper(
    val type: NamedApiResource
)