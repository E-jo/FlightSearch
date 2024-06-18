package com.example.flightsearch.ui.theme.screens

import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.data.SearchStringRepository
import com.example.flightsearch.ui.theme.AppViewModelProvider
import kotlinx.coroutines.coroutineScope


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val searchStringState by viewModel.searchString.collectAsState()
    val uiState by viewModel.airportListUiState.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        // Top row with text entry and search icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = searchStringState,
                onValueChange = { viewModel.updateSearchString(it) },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                    .padding(12.dp),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { viewModel.searchAirports(searchStringState) }) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_search),
                    contentDescription = "Search"
                )
            }
        }
        Text(
            text = "Flights to $searchStringState",
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    bottom = 8.dp
                )
        )
        /*
        // First LazyColumn
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            items(uiState.airportList, key = { airport -> airport.id }) { airport ->
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

         */
        Box(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp) // You can adjust this value as needed
            ) {
                items(uiState.airportList, key = { airport -> airport.id }) { airport ->
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
        Log.d("MainScreen", "Search string: $searchStringState")
        Log.d("MainScreen", "Results: ${uiState.airportList.size}")
        Log.d("MainScreen", "Airports: ${uiState.airportList.map { it.name }}")

        Spacer(modifier = Modifier.height(16.dp))

        // Second LazyColumn
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            items(20) { index ->
                Text(
                    text = "Detail $index",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.LightGray)
                        .padding(16.dp)
                )
            }
        }
    }
}

