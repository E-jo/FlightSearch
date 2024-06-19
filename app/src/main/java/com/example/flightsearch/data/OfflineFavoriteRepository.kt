package com.example.flightsearch.data

import com.example.flightsearch.models.Favorite
import kotlinx.coroutines.flow.Flow

class OfflineFavoriteRepository (
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {
    override fun getAllFavorites(): Flow<List<Favorite>> =
        favoriteDao.getAllFavorites()

    override fun getFavoriteById(id: Int): Flow<Favorite?> =
        favoriteDao.getFavoriteById(id)

    override fun getFavoriteByDepartureCodeAndDestinationCode(
        departureCode: String, destinationCode: String): Flow<Favorite?> =
        favoriteDao.getFavoriteByDepartureCodeAndDestinationCode(departureCode, destinationCode)


    override suspend fun insert(flight: Favorite): Long =
        favoriteDao.insert(flight)

    override suspend fun delete(flight: Favorite) =
        favoriteDao.delete(flight)

    override suspend fun getMaxId(): Int? =
        favoriteDao.getMaxId()

    override suspend fun resetFavoriteSequence() =
        favoriteDao.resetFavoriteSequence()

    override suspend fun clearFavorites() =
        favoriteDao.clearFavorites()
}

