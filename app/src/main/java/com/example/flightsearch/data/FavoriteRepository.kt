package com.example.flightsearch.data

import com.example.flightsearch.models.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllFavorites(): Flow<List<Favorite>>
    fun getFavoriteById(id: Int): Flow<Favorite?>
    fun getFavoriteByDepartureCodeAndDestinationCode(
        departureCode: String, destinationCode: String): Flow<Favorite?>

    suspend fun getMaxId(): Int?

    suspend fun resetFavoriteSequence()
    suspend fun clearFavorites()

    suspend fun insert(flight: Favorite): Long
    suspend fun delete(flight: Favorite)

}
