package com.example.flightsearch.ui.theme.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.AirportRepository
import com.example.flightsearch.data.SearchStringRepository
import com.example.flightsearch.models.Airport
import com.example.flightsearch.models.Flight
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val airportRepository: AirportRepository,
    private val searchStringRepository: SearchStringRepository
) : ViewModel() {

    val searchString: StateFlow<String> =
        searchStringRepository.searchString
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = ""
            )

/*
    important lesson: use flatMapLatest instead of searchString.value

    val airportListUiState: StateFlow<AirportListUiState> =
        airportRepository
            .getSuggestedAirports(searchString.value)
            .map { AirportListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = AirportListUiState()
            )

 */

/*
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val airportListUiState: StateFlow<AirportListUiState> =
        searchString
            .debounce(300)
            .flatMapLatest { query ->
                airportRepository
                    .getSuggestedAirports(query)
                    .map { AirportListUiState(it) }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = AirportListUiState()
            )


 */
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
        viewModelScope.launch {
            searchStringRepository.saveSearchString(searchString)
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
            results?.forEach {Log.d("MainScreen", it.name)}
        }
    }
}

data class UiState(
    val airportList: List<Airport> = listOf(),
    val resultList: List<Flight> = listOf(),
    val favoriteList: List<Flight> = listOf(),
    val currentAirport: Airport? = null,
    val isShowingResults: Boolean = false
)
