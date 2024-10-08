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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

// TODO : multiple issues with StateFlows being properly observed --- CHECKMARK!
// TODO : AppBar --- CHECKMARK!
// TODO : find better star icon for favorite button --- SCREW IT, WHO CARES?!
// TODO : reimplement the search button per project guidelines (actually, probably not,
//  that doesn't make sense in this implementation)
// TODO : AppBar --- add back navigation from results list back to search/favorites screen?
//  currently the way back is to delete what you've typed in the search bar, which isn't too intuitive

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val searchStringState by viewModel.searchString.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Column {
        FlightSearchAppBar()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
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
            }

            when (uiState.screenState) {
                ScreenState.SUGGESTIONS -> SuggestionList(viewModel = viewModel)
                ScreenState.RESULTS -> ResultList(viewModel = viewModel)
                ScreenState.FAVORITES -> FavoriteList(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightCard(flightResult: Favorite, viewModel: MainScreenViewModel) {
    val airportNames by viewModel.airportNames.collectAsState()

    LaunchedEffect(flightResult.departureCode) {
        viewModel.loadAirportName(flightResult.departureCode)
    }

    LaunchedEffect(flightResult.destinationCode) {
        viewModel.loadAirportName(flightResult.destinationCode)
    }

    Card(onClick = { }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Departure")
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = flightResult.departureCode,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = airportNames[flightResult.departureCode] ?: "Loading...")
                }
                Text(text = "Arrival")
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = flightResult.destinationCode,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = airportNames[flightResult.destinationCode] ?: "Loading...")
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
    val favorites by viewModel.favorites.collectAsState()
    val isFavorite = remember { mutableStateOf(favorites.contains(flightResult)) }
    val coroutineScope = rememberCoroutineScope()

    Log.d("MainScreen", "FavoriteButton recomposed: ${isFavorite.value}")
    Log.d("MainScreen", "FavoriteButton recomposed: ${flightResult.destinationCode}")

    key(isFavorite) {
        IconButton(onClick = {
            coroutineScope.launch {
                viewModel.toggleFavorite(flightResult)
                isFavorite.value = !isFavorite.value
            }
        }) {
            Icon(
                painter = painterResource(
                    id = if (isFavorite.value) R.drawable.star_filled
                    else R.drawable.star_unfilled),
                    contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionList(viewModel: MainScreenViewModel) {
    val searchStringState by viewModel.searchString.collectAsState()
    val airportList by viewModel.airportList.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Log.d("MainScreen", "Suggestion list recomposed: $searchStringState")
    Log.d("MainScreen", "Suggestion list recomposed: ${airportList.size}")

    Text(
        text = "Flights from $searchStringState",
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 8.dp)
    )
    if (airportList.isEmpty()) {
        Text(
            text = "No airports found matching $searchStringState",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, bottom = 8.dp)
        )
    } else {
        Box(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(
                    items = airportList,
                    key = { airport -> airport.id },
                ) { airport ->
                    Card(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.updateCurrentAirport(airport)
                                viewModel.getDestinations(airport.iataCode)
                            }
                        }
                    ) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)) {
                            Text(
                                text = airport.iataCode,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = airport.name)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultList(viewModel: MainScreenViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Log.d("MainScreen", "Result list recomposed: ${uiState.flightList.size}")


    if (uiState.flightList.isEmpty()) {
        Text(
            text = "No flights found from ${uiState.currentAirport?.name ?: ""}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, bottom = 8.dp)
        )
    } else {
        Text(
            text = "Flights from ${uiState.currentAirport?.name}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, bottom = 8.dp)
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
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
    val favorites by viewModel.favorites.collectAsState()

    Box(modifier = Modifier.fillMaxWidth()) {
        if (favorites.isEmpty()) {
            Text(
                text = "No current favorites selected",
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 8.dp, bottom = 8.dp)
            )
        } else {
            Column{
                Text(
                    text = "Current favorites",
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 8.dp, bottom = 8.dp)
                )
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchAppBar(
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = "Flight Search"
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier,
    )
}
