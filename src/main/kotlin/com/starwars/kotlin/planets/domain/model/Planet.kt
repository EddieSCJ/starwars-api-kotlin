package com.starwars.kotlin.planets.domain.model

data class Planet(
    val id: String? = null,
    val name: String,
    val climate: List<String>,
    val terrain: List<String>,
    val movieAppearances: Int,
    val cacheInDays: Long
)