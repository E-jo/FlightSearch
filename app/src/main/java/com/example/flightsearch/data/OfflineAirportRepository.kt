package com.example.flightsearch.data

import com.example.flightsearch.models.Airport
import kotlinx.coroutines.flow.Flow

class OfflineAirportRepository(
    private val airportDao: AirportDao
) : AirportRepository {
    override fun getAllAirports(): Flow<List<Airport>> =
        airportDao.getAllAirports()

    override fun getSuggestedAirports(searchString: String): Flow<List<Airport>> =
        airportDao.getSuggestedAirports(searchString)

    override fun getAirportById(id: Int): Flow<Airport?> =
        airportDao.getAirportById(id)

    override suspend fun insertAirport(airport: Airport) {
        airportDao.insert(airport)
    }

    override suspend fun deleteAirport(airport: Airport) {
        airportDao.delete(airport)
    }

    override suspend fun updateAirport(airport: Airport) {
        airportDao.update(airport)
    }
}
