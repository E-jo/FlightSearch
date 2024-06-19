package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.flightsearch.models.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    fun getAllFavorites(): Flow<List<Favorite>>
    @Query("SELECT * FROM favorite WHERE id = :id")

    fun getFavoriteById(id: Int): Flow<Favorite?>
    @Insert
    suspend fun insertFavorite(flight: Favorite)
    @Delete
    suspend fun deleteFavorite(flight: Favorite)
}
