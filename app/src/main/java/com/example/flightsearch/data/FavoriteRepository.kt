package com.example.flightsearch.data

import com.example.flightsearch.models.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllFavorites(): Flow<List<Favorite>>
    fun getFavoriteById(id: Int): Flow<Favorite?>
    suspend fun insertFavorite(flight: Favorite)
    suspend fun deleteFavorite(flight: Favorite)
}
