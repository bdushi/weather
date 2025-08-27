package al.bruno.domain.weather.repository

import al.bruno.domain.weather.model.Coord

interface LocationRepository {
    var isMyLocationEnabled: Boolean
    fun fetchLocation(onLocationUpdate: (Coord) -> Unit)
    fun getLocation(): Coord
}