package com.example.flightsearch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

interface AppContainer {
    val searchStringRepository: SearchStringRepository
    val favoriteRepository: FavoriteRepository
    val airportRepository: AirportRepository
}

private const val SEARCH_STRING_NAME = "search_string"

class AppDataContainer(private val context: Context) : AppContainer {
    private val Context.dataStore: DataStore<Preferences>
            by preferencesDataStore(name = SEARCH_STRING_NAME)

    private val database: FlightSearchDatabase by lazy { FlightSearchDatabase.getDatabase(context) }

    override val searchStringRepository: SearchStringRepository by lazy {
        SearchStringRepository(context.dataStore)
    }

    override val favoriteRepository: FavoriteRepository by lazy {
        OfflineFavoriteRepository(database.favoriteDao())
    }

    override val airportRepository: AirportRepository by lazy {
        OfflineAirportRepository(database.airportDao())
    }

}
