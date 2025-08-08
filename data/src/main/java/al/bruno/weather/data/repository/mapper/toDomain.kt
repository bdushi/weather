package al.bruno.weather.data.repository.mapper

import al.bruno.domain.weather.model.City
import al.bruno.domain.weather.model.Clouds
import al.bruno.domain.weather.model.Condition
import al.bruno.domain.weather.model.Coord
import al.bruno.domain.weather.model.Forecast
import al.bruno.domain.weather.model.ForecastWeather
import al.bruno.domain.weather.model.Main
import al.bruno.domain.weather.model.Sys
import al.bruno.domain.weather.model.Weather
import al.bruno.domain.weather.model.Wind
import al.bruno.weather.data.network.model.CityResponse
import al.bruno.weather.data.network.model.CloudsResponse
import al.bruno.weather.data.network.model.ConditionResponse
import al.bruno.weather.data.network.model.CoordResponse
import al.bruno.weather.data.network.model.ForecastResponse
import al.bruno.weather.data.network.model.ForecastWeatherResponse
import al.bruno.weather.data.network.model.MainResponse
import al.bruno.weather.data.network.model.WeatherResponse
import al.bruno.weather.data.network.model.WindResponse


fun CoordResponse.toCoord() = Coord(
    lon = lon,
    lat = lat
)

fun MainResponse.toMain() = Main(
    temp = temp,
    feelsLike = feelsLike,
    tempMin = tempMin,
    tempMax = tempMax,
    pressure = pressure,
    humidity = humidity,
    seaLevel = seaLevel,
    groundLevel = groundLevel
)

fun ConditionResponse.toWeatherCondition() = Condition(
    id = id,
    main = main,
    description = description,
    icon = icon
)

fun CloudsResponse.toClouds() = Clouds(
    all = all
)

fun WindResponse.toWind() = Wind(
    speed = speed,
    deg = deg,
    gust = gust
)

fun WeatherResponse.toWeather() = Weather(
    id = id,
    coord = coord.toCoord(),
    weather = weather.map {
        it.toWeatherCondition()
    },
    base = base,
    main = main.toMain(),
    visibility = visibility,
    wind = wind.toWind(),
    clouds = clouds.toClouds(),
    dt = dt,
    sys = Sys(
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

fun ForecastWeatherResponse.toForecastWeather() = ForecastWeather(
    dt = dt,
    main = main.toMain(),
    weather = weather.map { it.toWeatherCondition() },
    clouds = clouds.toClouds(),
    wind = wind.toWind(),
    visibility = visibility,
    pop = pop,
    dtTxt = dtTxt
)

fun CityResponse.toCity() = City(
    id = id,
    name = name,
    coord = coord.toCoord(),
    country = country,
    population = population,
    timezone = timezone,
    sunrise = sunrise,
    sunset = sunset
)

fun ForecastResponse.toForecast() = Forecast(
    cod = cod,
    message = message,
    cnt = cnt,
    weather = list.map { it.toForecastWeather() },
    city = city.toCity()
)