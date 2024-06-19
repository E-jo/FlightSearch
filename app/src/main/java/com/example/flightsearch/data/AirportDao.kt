package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.flightsearch.models.Airport
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("SELECT * FROM airport WHERE id = :id")
    fun getAirportById(id: Int): Flow<Airport?>

    @Query("SELECT * FROM airport WHERE iata_code = :iataCode")
    fun getAirportByIata(iataCode: String): Flow<Airport?>

    @Query("SELECT * FROM airport ORDER BY passengers DESC")
    fun getAllAirports(): Flow<List<Airport>>

    @Query("""
        SELECT * FROM airport 
        WHERE name LIKE '%' || :searchString || '%' 
        OR iata_code LIKE '%' || :searchString || '%' 
        ORDER BY passengers DESC
    """)
    fun getSuggestedAirports(searchString: String): Flow<List<Airport>>

    @Query("""
        SELECT * FROM airport 
        WHERE iata_code NOT LIKE '%' || :searchString || '%' 
        ORDER BY passengers DESC
    """)
    fun getDestinationAirports(searchString: String): Flow<List<Airport>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(airport: Airport)

    @Update
    suspend fun update(airport: Airport)

    @Delete
    suspend fun delete(airport: Airport)

    @Query("SELECT * FROM airport WHERE id = :id")
    fun getAirport(id: Int): Flow<Airport>


    @Query("SELECT name FROM airport WHERE id != :id ORDER BY passengers DESC")
    fun getAllFlightsFromAirport(id: Int): Flow<List<String>>

}
