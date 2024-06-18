package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import com.example.flightsearch.models.Flight
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {

    @Query("SELECT * FROM favorite WHERE id = :id")
    fun getFlightResult(id: Int): Flow<Flight>


}
