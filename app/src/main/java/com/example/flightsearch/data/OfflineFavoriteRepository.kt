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

    override suspend fun insertFavorite(flight: Favorite) =
        favoriteDao.insertFavorite(flight)

    override suspend fun deleteFavorite(flight: Favorite) =
        favoriteDao.deleteFavorite(flight)

}

