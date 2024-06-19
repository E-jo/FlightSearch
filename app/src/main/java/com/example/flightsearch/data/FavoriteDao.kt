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

    @Query("""
        SELECT * FROM favorite 
        WHERE departure_code = :departureCode 
        AND destination_code = :destinationCode
        """)
    fun getFavoriteByDepartureCodeAndDestinationCode(
        departureCode: String, destinationCode: String): Flow<Favorite?>

    @Query("SELECT MAX(id) FROM favorite")
    suspend fun getMaxId(): Int?

    @Query("DELETE FROM sqlite_sequence WHERE name='favorite'")
    suspend fun resetFavoriteSequence()

    @Query("DELETE FROM favorite")
    suspend fun clearFavorites()
    @Insert
    suspend fun insert(flight: Favorite): Long
    @Delete
    suspend fun delete(flight: Favorite)

}
