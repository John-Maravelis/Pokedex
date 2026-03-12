package com.john.desquared.pokemon.model

data class Pokemon(
    val id: Int,
    val name: String,
    val type: String,
    val imageUrl: String,
    val hp: Int = 0,
    val attack: Int = 0,
    val defense: Int = 0
)