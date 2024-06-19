package com.example.flightsearch.ui.theme.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.AirportRepository
import com.example.flightsearch.data.FavoriteRepository
import com.example.flightsearch.data.SearchStringRepository
import com.example.flightsearch.models.Airport
import com.example.flightsearch.models.FlightResult
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

class MainScreenViewModel(
    private val airportRepository: AirportRepository,
    private val favoriteRepository: FavoriteRepository,
    private val searchStringRepository: SearchStringRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(
            currentAirport = null,
            isShowingResults = false
        )
    )
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
            it.copy(isShowingResults = true)
        }
    }

    fun hideResults() {
        _uiState.update {
            it.copy(isShowingResults = false)
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
            val flights = mutableListOf<FlightResult>()
            var index = 0
            results?.forEach {
                Log.d("MainScreen", it.name)
                var currentAirportName = ""
                if (_uiState.value.currentAirport != null) {
                    currentAirportName = _uiState.value.currentAirport!!.name
                }
                val flight = FlightResult(
                    index++,
                    currentAirportName,
                    it.name,
                )
                flights.add(flight)
            }
            _uiState.update {
                it.copy(
                    flightList = flights,
                    isShowingResults = true
                )
            }
        }
    }
}

data class UiState(
    val currentAirport: Airport? = null,
    val isShowingResults: Boolean = false,
    val flightList: List<FlightResult> = emptyList(),
    val screenState: ScreenState = ScreenState.SUGGESTIONS
)

enum class ScreenState { SUGGESTIONS, RESULTS, FAVORITES }
