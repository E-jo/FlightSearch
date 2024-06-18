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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.debounce

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
                ?.size
            Log.d("MainScreen", "Search results for $searchStringState: $results")
        }
    }
}

data class FlightListUiState(val flightList: List<Flight> = listOf())
data class AirportListUiState(val airportList: List<Airport> = listOf())
data class SuggestionListUiState(val flightList: List<Flight> = listOf())
