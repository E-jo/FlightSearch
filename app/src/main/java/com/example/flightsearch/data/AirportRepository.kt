package com.example.flightsearch.data

import com.example.flightsearch.models.Airport
import kotlinx.coroutines.flow.Flow

interface AirportRepository {
    fun getAllAirports(): Flow<List<Airport>>
    fun getAirportById(id: Int): Flow<Airport?>
    fun getSuggestedAirports(searchString: String): Flow<List<Airport>>
    suspend fun insertAirport(airport: Airport)
    suspend fun deleteAirport(airport: Airport)
    suspend fun updateAirport(airport: Airport)
}
