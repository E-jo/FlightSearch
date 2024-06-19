package com.example.flightsearch.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.flightsearch.ui.theme.AppViewModelProvider

// TODO : properly spaced FlightCard LazyColumn
// TODO : get star icon showing
// TODO : save/unsave favorite functionality from repo
// TODO : replace isShowingResults with 3 state enum ScreenState



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val searchStringState by viewModel.searchString.collectAsState()
    val airportList by viewModel.airportList.collectAsState()
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
                    viewModel.hideResults()
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

        if (!uiState.isShowingResults) {
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
        } else {
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
                            key = { flightResult -> flightResult.id }
                        ) {
                            /*
                        Text(
                            text = "Departure: ${it.departureCode} - " +
                                    "Destination: ${it.destinationCode}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )

                         */
                            FlightCard()
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightCard() {
    Card(
        onClick = { }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column {
                Text(
                    text = "Departure"
                )
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(
                        text = "EXP",
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Name of airport",
                    )
                }
                Text(
                    text = "Arrival"
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "EXP",
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Name of airport",
                    )
                }
            }
            FavoriteButton(isFavorite = true){ }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}
@Composable
fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = if (isFavorite)
                R.drawable.star_filled else R.drawable.star_unfilled),
            contentDescription = null
        )
    }
}
