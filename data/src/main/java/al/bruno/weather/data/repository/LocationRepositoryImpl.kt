package al.bruno.weather.data.repository

import al.bruno.domain.weather.model.Coord
import al.bruno.domain.weather.repository.LocationRepository
import android.Manifest
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(private val locationProvider: FusedLocationProviderClient) :
    LocationRepository {
    override var isMyLocationEnabled: Boolean = false

    //        get() = TODO("Not yet implemented")
//        set(value) {}
    private var currentLocation = Location("default_location")
        .apply {
            this.latitude = DEFAULT_LAT
            this.longitude = DEFAULT_LNG
        }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun fetchLocation(onLocationUpdate: (Coord) -> Unit) {
        locationProvider
            .lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    currentLocation = it
                    onLocationUpdate(Coord(it.longitude, it.latitude))
                }
            }
    }

    override fun getLocation(): Coord = Coord(currentLocation.longitude, currentLocation.latitude)
}

const val DEFAULT_LAT = 42.4046
const val DEFAULT_LNG = 19.7681