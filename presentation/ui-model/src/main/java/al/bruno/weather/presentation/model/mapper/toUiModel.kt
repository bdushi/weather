package al.bruno.weather.presentation.model.mapper

import al.bruno.domain.weather.model.CacheSearch
import al.bruno.domain.weather.model.City
import al.bruno.domain.weather.model.Clouds
import al.bruno.domain.weather.model.Condition
import al.bruno.domain.weather.model.Coord
import al.bruno.domain.weather.model.Forecast
import al.bruno.domain.weather.model.ForecastWeather
import al.bruno.domain.weather.model.Main
import al.bruno.domain.weather.model.Weather
import al.bruno.domain.weather.model.Wind
import al.bruno.weather.presentation.model.CacheSearchUiModel
import al.bruno.weather.presentation.model.CityUiModel
import al.bruno.weather.presentation.model.CloudsUiModel
import al.bruno.weather.presentation.model.ConditionUiModel
import al.bruno.weather.presentation.model.CoordUiModel
import al.bruno.weather.presentation.model.ForecastUiModel
import al.bruno.weather.presentation.model.ForecastWeatherUiModel
import al.bruno.weather.presentation.model.MainUiModel
import al.bruno.weather.presentation.model.SysUiModel
import al.bruno.weather.presentation.model.WeatherUiModel
import al.bruno.weather.presentation.model.WindUiModel


fun Coord.toCoordUiModel() = CoordUiModel(
    lon = lon,
    lat = lat
)

fun Main.toMainUiModel() = MainUiModel(
    temp = temp,
    feelsLike = feelsLike,
    tempMin = tempMin,
    tempMax = tempMax,
    pressure = pressure,
    humidity = humidity,
    seaLevel = seaLevel,
    groundLevel = groundLevel
)

fun Condition.toWeatherConditionUiModel() = ConditionUiModel(
    id = id,
    main = main,
    description = description,
    icon = icon
)

fun Clouds.toCloudsUiModel() = CloudsUiModel(
    all = all
)

fun Wind.toWindUiModel() = WindUiModel(
    speed = speed,
    deg = deg,
    gust = gust
)

fun Weather.toWeatherUiModel() = WeatherUiModel(
    id = id,
    coord = coord.toCoordUiModel(),
    weather = weather.map { it.toWeatherConditionUiModel() },
    base = base,
    main = main.toMainUiModel(),
    visibility = visibility,
    wind = wind.toWindUiModel(),
    clouds = clouds.toCloudsUiModel(),
    dt = dt,
    sys = SysUiModel(
        id = sys.id,
        type = sys.type,
        country = sys.country,
        sunset = sys.sunset,
        sunrise = sys.sunrise
    ),
    timezone = timezone,
    name = name,
    cod = cod
)

fun ForecastWeather.toForecastWeatherUiModel() = ForecastWeatherUiModel(
    dt = dt,
    main = main.toMainUiModel(),
    weather = weather.map { it.toWeatherConditionUiModel() },
    clouds = clouds.toCloudsUiModel(),
    wind = wind.toWindUiModel(),
    visibility = visibility,
    pop = pop,
    dtTxt = dtTxt
)

fun City.toCityUiModel() = CityUiModel(
    id = id,
    name = name,
    coord = coord.toCoordUiModel(),
    country = country,
    population = population,
    timezone = timezone,
    sunrise = sunrise,
    sunset = sunset
)

fun Forecast.toForecastUiModel() = ForecastUiModel(
    cod = cod,
    message = message,
    cnt = cnt,
    weather = weather.map { it.toForecastWeatherUiModel() },
    city = city.toCityUiModel()
)

fun CacheSearch.toCacheSearchUiModel() = CacheSearchUiModel(id = id, query = query)

fun List<CacheSearch>.toCacheSearchUiModelList() = map {
    it.toCacheSearchUiModel()
}