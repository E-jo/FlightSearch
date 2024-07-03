package com.example.flightsearch.ui.theme.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.AirportRepository
import com.example.flightsearch.data.FavoriteRepository
import com.example.flightsearch.data.SearchStringRepository
import com.example.flightsearch.models.Airport
import com.example.flightsearch.models.Favorite
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainScreenViewModel(
    private val airportRepository: AirportRepository,
    private val favoriteRepository: FavoriteRepository,
    private val searchStringRepository: SearchStringRepository
) : ViewModel() {
    init {
        clearFavorites()
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _searchString = MutableStateFlow("")
    val searchString: StateFlow<String> = _searchString

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val airportList: StateFlow<List<Airport>> =
        searchString
            .debounce(300)
            .flatMapLatest { query ->
                airportRepository
                    .getSuggestedAirports(query)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )

    private val _airportNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val airportNames: StateFlow<Map<String, String>> = _airportNames

    fun loadAirportName(iataCode: String) {
        viewModelScope.launch {
            val name = airportRepository
                .getAirportNameByIataCode(iataCode)
                .firstOrNull() ?: "Unknown"
            _airportNames.value += (iataCode to name)
        }
    }

    private val _favorites = MutableStateFlow<List<Favorite>>(emptyList())
    val favorites: StateFlow<List<Favorite>> get() = _favorites

    fun loadFavorites() {
        viewModelScope.launch {
            val favorites = favoriteRepository
                .getAllFavorites()
                .firstOrNull() ?: emptyList()
            _favorites.value = favorites
        }
    }

    private fun clearFavorites() {
        viewModelScope.launch {
            favoriteRepository.clearFavorites()
            favoriteRepository.resetFavoriteSequence()
        }
    }
    fun updateSearchString(searchString: String) {
        _searchString.value = searchString
        viewModelScope.launch {
            searchStringRepository.saveSearchString(searchString)
        }
    }

    fun updateCurrentAirport(airport: Airport) {
        _uiState.update {
            it.copy(currentAirport = airport)
        }
    }

    fun showResults() {
        _uiState.update {
            it.copy(screenState = ScreenState.RESULTS)
        }
    }

    fun hideResults(searchString: String) {
        _uiState.update {
            if (searchString.isEmpty()) {
                it.copy(screenState = ScreenState.FAVORITES)
            } else {
                it.copy(screenState = ScreenState.SUGGESTIONS)
            }
        }
    }

    fun searchAirports(searchStringState: String) {
        viewModelScope.launch {
            val results = airportRepository
                .getSuggestedAirports(searchStringState)
                .firstOrNull()
            Log.d(
                "MainScreen",
                "Search results for $searchStringState: ${results?.size ?: 0}"
            )

            results?.forEach {Log.d("MainScreen", it.name)}
        }
    }
    fun getDestinations(searchStringState: String) {
        viewModelScope.launch {
            val results = airportRepository
                .getDestinationAirports(searchStringState)
                .firstOrNull()
            Log.d(
                "MainScreen",
                "Destination results for $searchStringState: ${results?.size ?: 0}"
            )
            val flights = mutableListOf<Favorite>()
            results?.forEach {
                Log.d("MainScreen", it.name)
                var currentAirportCode = ""
                if (_uiState.value.currentAirport != null) {
                    currentAirportCode = _uiState.value.currentAirport!!.iataCode
                }
                val flight = Favorite(
                    departureCode = currentAirportCode,
                    destinationCode = it.iataCode
                )
                val generatedId = favoriteRepository.insert(flight)
                Log.d("ViewModel", "Inserted Favorite with ID: $generatedId")
                val flightWithId =
                    favoriteRepository
                        .getFavoriteByDepartureCodeAndDestinationCode(
                            flight.departureCode,
                            flight.destinationCode
                        )
                        .firstOrNull()
                flightWithId?.let {
                    favoriteRepository.delete(flightWithId)
                    flights.add(flightWithId)
                }

            }
            flights.forEach {
                Log.d("ViewModel", "Flight: ${it.id}")
            }
            _uiState.update {
                it.copy(
                    flightList = flights,
                    screenState = ScreenState.RESULTS
                )
            }
        }
    }

    fun toggleFavorite(flight: Favorite) {
        Log.d("ViewModel", "Toggling favorite for ${flight.id}")
        viewModelScope.launch {
            if (favorites.value.contains(flight)) {
                Log.d("ViewModel", "${flight.id} is currently a favorite, removing it")
                favoriteRepository.delete(flight)
            } else {
                Log.d("ViewModel", "${flight.id} is not currently a favorite, adding it")
                favoriteRepository.insert(flight)
            }
        }
        loadFavorites()
    }
    
    fun isFavorite(flight: Favorite): Boolean {
        var isFavorite = false
        runBlocking {
            isFavorite = favoriteRepository
                .getFavoriteByDepartureCodeAndDestinationCode(
                    flight.departureCode,
                    flight.destinationCode
                )
                .firstOrNull()
                .let { it != null }
        }
        return isFavorite
    }

    fun getAirportNameByIataCode(iataCode: String): String {
        Log.d("ViewModel", "Getting airport name for $iataCode")
        var name = ""
        runBlocking {
            name = airportRepository
            .getAirportNameByIataCode(iataCode)
            .firstOrNull()
            .let { it ?: "" }
            Log.d("ViewModel", "Inside CoroutineScope: name = $name")
        }
        Log.d("ViewModel", "Airport name for $iataCode: $name")
        return name
    }
}

data class UiState(
    val currentAirport: Airport? = null,
    val flightList: List<Favorite> = emptyList(),
    val screenState: ScreenState = ScreenState.SUGGESTIONS
)

enum class ScreenState { SUGGESTIONS, RESULTS, FAVORITES }



