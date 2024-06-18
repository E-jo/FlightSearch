package com.example.flightsearch.data

import com.example.flightsearch.models.Flight
import kotlinx.coroutines.flow.Flow

interface FlightRepository {
    fun getAllFlightResults(): Flow<List<Flight>>
    fun getFlightResultById(id: Int): Flow<Flight?>
    suspend fun insertFlightResult(flight: Flight)
    suspend fun deleteFlightResult(flight: Flight)
}
