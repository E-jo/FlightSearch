package com.example.flightsearch.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.R
import com.example.flightsearch.models.Favorite
import com.example.flightsearch.ui.theme.AppViewModelProvider

// TODO : multiple issues with SharedFlows being properly observed
// TODO : reimplement the search button per project guidelines (actually, probably not,
//  that doesn't make sense in this implementation)
// TODO : AppBar
// TODO : find better star icon for favorite button

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val searchStringState by viewModel.searchString.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchStringState,
                onValueChange = {
                    viewModel.updateSearchString(it)
                    viewModel.hideResults(it)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                    .padding(12.dp),
                textStyle = TextStyle(fontSize = 16.sp),
                placeholder = { Text(text = "Enter departure airport") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            /*
            IconButton(onClick = {
                viewModel.getDestinations(searchStringState)
                //viewModel.searchAirports(searchStringState)
            }) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_search),
                    contentDescription = "Search"
                )
            }

<a href="https://www.flaticon.com/free-icons/star" title="star icons">Star icons created by Pixel perfect - Flaticon</a>
             */
        }

        when (uiState.screenState) {
            ScreenState.SUGGESTIONS -> SuggestionList(viewModel = viewModel)
            ScreenState.RESULTS -> ResultList(viewModel = viewModel)
            ScreenState.FAVORITES -> FavoriteList(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightCard(flightResult: Favorite, viewModel: MainScreenViewModel) {
    val airportNames by viewModel.airportNames.collectAsState()

    // Load airport name for departureCode when flightResult.departureCode changes
    LaunchedEffect(flightResult.departureCode) {
        viewModel.loadAirportName(flightResult.departureCode)
    }

    // Load airport name for destinationCode when flightResult.destinationCode changes
    LaunchedEffect(flightResult.destinationCode) {
        viewModel.loadAirportName(flightResult.destinationCode)
    }

    Card(
        onClick = { }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Departure"
                )
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(
                        text = flightResult.departureCode,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = airportNames[flightResult.departureCode] ?: "Loading...",
                    )
                }
                Text(
                    text = "Arrival"
                )
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(
                        text = flightResult.destinationCode,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = airportNames[flightResult.destinationCode] ?: "Loading...",
                    )
                }
            }
            FavoriteButton(flightResult, viewModel)
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}
@Composable
fun FavoriteButton(
    flightResult: Favorite,
    viewModel: MainScreenViewModel
) {
    viewModel.loadFavorites()
    val favorites by viewModel.favorites.collectAsState()
    var isFavorite = favorites.contains(flightResult)
    LaunchedEffect(favorites) {
        //viewModel.loadFavorites()
        isFavorite = favorites.contains(flightResult)
    }

    IconButton(onClick = { viewModel.toggleFavorite(flightResult) }) {
        Icon(
            painter = painterResource(id = if (isFavorite)
                R.drawable.star_filled else R.drawable.star_unfilled),
            contentDescription = null
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionList(viewModel: MainScreenViewModel) {
    val searchStringState by viewModel.searchString.collectAsState()
    val airportList by viewModel.airportList.collectAsState()

    Text(
        text = "Flights from $searchStringState",
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 8.dp,
                bottom = 8.dp
            )
    )
    if (airportList.isEmpty()) {
        Text(
            text = "No airports found matching $searchStringState",
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    bottom = 8.dp
                )
        )
    } else {
        Box(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(
                    items = airportList,
                    key = { airport -> airport.id },
                ) { airport ->
                    Card(
                        onClick = {
                            viewModel.updateCurrentAirport(airport)
                            viewModel.getDestinations(airport.iataCode)
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = airport.iataCode,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = airport.name,
                            )
                        }
                    }
                }
            }
        }
        Log.d("MainScreen", "Search string: $searchStringState")
        Log.d("MainScreen", "Results: ${airportList.size}")
        Log.d("MainScreen", "Airports: ${airportList.map { it.name }}")
    }
}

@Composable
fun ResultList(viewModel: MainScreenViewModel) {
    val searchStringState by viewModel.searchString.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.flightList.isEmpty()) {
        Text(
            text = "No flights found from $searchStringState",
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    bottom = 8.dp
                )
        )
    } else {
        Text(
            text = "Flights from ${uiState.currentAirport?.name}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    bottom = 8.dp
                )
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(
                    items = uiState.flightList,
                    key = { flightResult -> flightResult.id!! }
                ) {
                    FlightCard(it, viewModel)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun FavoriteList(viewModel: MainScreenViewModel) {
    viewModel.loadFavorites()
    val favorites by viewModel.favorites.collectAsState()
    LaunchedEffect(favorites) {
        viewModel.loadFavorites()
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        if (favorites.isEmpty()) {
            Text(
                text = "No favorites found",
                modifier = Modifier
                    .fillMaxWidth()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(
                    items = favorites,
                    key = { flightResult -> flightResult.id }
                ) {
                    FlightCard(it, viewModel)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}