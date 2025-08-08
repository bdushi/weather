import al.bruno.presentation.ui.R
import al.bruno.weather.presentation.model.WeatherUiModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun WeatherComponent(
    weatherUiModel: WeatherUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(top = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.weather_now),
                style = MaterialTheme.typography.titleLarge
            )
            Row(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    text = "${weatherUiModel.main.temp.toInt()}\u00B0",
                    style = MaterialTheme.typography.displayLarge
                )
                AsyncImage(
                    alignment = Alignment.Center,
                    model = "https://openweathermap.org/img/wn/${weatherUiModel.weather.first().icon}@4x.png",
                    contentDescription = weatherUiModel.weather.first().description,
                    modifier = Modifier.weight(1f),
                )
            }
            Row {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    text = "High: ${weatherUiModel.main.tempMax.toInt()}\u00B0",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    text = "Low: ${weatherUiModel.main.tempMin.toInt()}\u00B0",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = weatherUiModel.weather.first().main,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Feels Like: ${weatherUiModel.main.feelsLike.toInt()}\u00B0",
            )
        }
    }
}