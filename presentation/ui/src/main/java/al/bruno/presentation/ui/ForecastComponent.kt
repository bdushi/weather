package al.bruno.presentation.ui

import al.bruno.weather.presentation.model.ForecastUiModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import java.time.format.DateTimeFormatter

@Composable
fun ForecastComponent(
    modifier: Modifier = Modifier,
    forecastUiModel: ForecastUiModel
) {
    Column(
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.forecast),
            style = MaterialTheme.typography.titleLarge
        )
        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            itemsIndexed(
                items = forecastUiModel.weather,
                key = { _, item -> item.dtTxt },
            ) { index, weatherUiModel ->
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 4.dp),
                        text = "${weatherUiModel.main.temp.toInt()}\u00B0",
                    )
                    AsyncImage(
                        alignment = Alignment.Center,
                        model = "https://openweathermap.org/img/wn/${weatherUiModel.weather.first().icon}@4x.png",
                        contentDescription = weatherUiModel.weather.first().description,
                    )
                    Text(
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        text = weatherUiModel.dt.format(DateTimeFormatter.ofPattern("HH:mm")),
                    )
                }
            }
        }
    }
}