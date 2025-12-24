package al.bruno.presentation.weather

import WeatherComponent
import al.bruno.presentation.ui.ContactItem
import al.bruno.presentation.ui.ErrorContentComponent
import al.bruno.presentation.ui.ForecastComponent
import al.bruno.presentation.ui.LoadingContentComponent
import al.bruno.presentation.ui.R
import al.bruno.presentation.ui.SearchEngine
import al.bruno.weather.presentation.model.UIState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier
) {
    val weatherViewModel: WeatherViewModel = koinViewModel()
    val weatherUIState by weatherViewModel.state.collectAsStateWithLifecycle()
    // Collect one-time effects
    LaunchedEffect(Unit) {
        weatherViewModel.effects.collect { effect ->
            when (effect) {
                is WeatherUIEffect.ShowError -> {
//                    snackbarHostState.showSnackbar(
//                        message = effect.error,
//                        duration = SnackbarDuration.Long
//                    )
                }

                is WeatherUIEffect.ShowToast -> {
//                    snackbarHostState.showSnackbar(
//                        message = effect.message,
//                        duration = SnackbarDuration.Short
//                    )
                }
            }
        }
    }
    WeatherComponent(
        modifier = modifier,
        weatherUIState = weatherUIState,
        processWeatherUIEvent = weatherViewModel::sendEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherComponent(
    weatherUIState: WeatherUIState,
    processWeatherUIEvent: (WeatherUIEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    when (val state = weatherUIState.uIState) {
        is UIState.Error -> {
            ErrorContentComponent(
                onRetry = {
                    processWeatherUIEvent(
                        WeatherUIEvent.OnRetry
                    )
                },
                errorMessages = state.error ?: stringResource(R.string.general_error_message),
                errorButton = stringResource(R.string.re_try)
            )
        }

        is UIState.Loading -> {
            LoadingContentComponent(
                text = stringResource(R.string.loading)
            )
        }

        is UIState.Success -> {
            Column(
                modifier = modifier
                    .padding(12.dp)
                    .fillMaxSize()
            ) {
                SearchEngine(
                    query = weatherUIState.query,
                    itemsList = weatherUIState.cacheSearch,
                    modifier = Modifier.fillMaxWidth(),
                    onQueryChange = { query ->
                        processWeatherUIEvent(
                            WeatherUIEvent.OnQueryChange(query)
                        )
                    },
                    onSearch = { query ->
                        processWeatherUIEvent(
                            WeatherUIEvent.Search(query)
                        )
                    },
                    onItemSelected = { query ->
                        processWeatherUIEvent(
                            WeatherUIEvent.OnSelectedItems(query.query)
                        )
                    },
                    placeholder = { Text(text = stringResource(id = R.string.search)) },
                    contentDescription = stringResource(id = R.string.search),
                    shape = RoundedCornerShape(8.dp),
                    itemContent = { t, onClick ->
                        ContactItem(
                            contact = t, onClick = onClick, onDelete = {
                                processWeatherUIEvent(
                                    WeatherUIEvent.OnDeleteCacheSearch(it)
                                )
                            })
                    })
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(top = 8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        weatherUIState.weatherUiModel?.let { weatherUiModel ->
                            WeatherComponent(weatherUiModel = weatherUiModel)
                        } ?: run {
                            ErrorContentComponent(
                                onRetry = {
                                    processWeatherUIEvent(
                                        WeatherUIEvent.OnRetry
                                    )
                                },
                                errorMessages = stringResource(R.string.general_error_message),
                                errorButton = stringResource(R.string.re_try)
                            )
                        }
                    }
                    item {
                        weatherUIState.forecastUiModel?.let { forecastUiModel ->
                            ForecastComponent(forecastUiModel = forecastUiModel)
                        } ?: run {
                            ErrorContentComponent(
                                onRetry = {
                                    processWeatherUIEvent(
                                        WeatherUIEvent.OnRetry
                                    )
                                },
                                errorMessages = stringResource(R.string.general_error_message),
                                errorButton = stringResource(R.string.re_try)
                            )
                        }
                    }
                }
            }
        }
    }
}