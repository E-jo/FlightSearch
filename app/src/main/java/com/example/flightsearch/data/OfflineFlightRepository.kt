package com.example.flightsearch.data

import com.example.flightsearch.models.Flight
import kotlinx.coroutines.flow.Flow

class OfflineFlightRepository (
    private val flightDao: FlightDao
) : FlightRepository {
    override fun getAllFlightResults(): Flow<List<Flight>> {
        TODO("Not yet implemented")
    }

    override fun getFlightResultById(id: Int): Flow<Flight?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertFlightResult(flight: Flight) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFlightResult(flight: Flight) {
        TODO("Not yet implemented")
    }

}

