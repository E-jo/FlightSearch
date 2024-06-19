package com.example.flightsearch.ui.theme

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.ui.theme.screens.MainScreenViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
            initializer {
                MainScreenViewModel(
                    flightSearchApplication().container.airportRepository,
                    flightSearchApplication().container.favoriteRepository,
                    flightSearchApplication().container.searchStringRepository
                )
            }
     }
}

fun CreationExtras.flightSearchApplication(): FlightSearchApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
            as FlightSearchApplication)
