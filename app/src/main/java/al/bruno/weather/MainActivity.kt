package al.bruno.weather

import al.bruno.presentation.ui.R
import al.bruno.presentation.ui.theme.WeatherTheme
import al.bruno.presentation.weather.WeatherScreen
import al.bruno.presentation.weather.WeatherUIEffect
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class MainActivity : ComponentActivity() {
    // Consolidated permission launcher that handles both location and notification permissions
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        handlePermissionResults(permissions)
    }
    private fun handlePermissionResults(permissions: Map<String, Boolean>) {
        val hasLocationPermission = hasLocationPermission()
        val hasNotificationPermission = hasNotificationPermission()
//        if (hasLocationPermission) {
//            weatherViewModel.fetchWeatherData()
//        } else {
//            showPermissionDeniedMessage()
//        }
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // No permission needed for API < 33
        }
    }

    private fun showPermissionDeniedMessage() {
        Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.permission_denied),
            Snackbar.LENGTH_LONG
        ).setAction(R.string.settings) {
            openAppSettings()
        }.show()
    }

    private fun openAppSettings() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                "package:${BuildConfig.APPLICATION_ID}".toUri()
            )
        )
    }

    // Main permission checking method - handles both location and notification
    fun checkAllPermissions() {
        val permissionsToRequest = getPermissionsToRequest()

        if (permissionsToRequest.isEmpty()) {
            // All permissions granted
//            weatherViewModel.fetchWeatherData()
            return
        }

        val shouldShowRationale = permissionsToRequest.any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
        }

        if (shouldShowRationale) {
            showPermissionRationaleDialog(permissionsToRequest)
        } else {
            requestPermissions(permissionsToRequest)
        }
    }
    // Location-only permission check (for your onLocationClick)
    fun checkLocationPermission() {
        if (hasLocationPermission()) {
//            weatherViewModel.fetchWeatherData()
            return
        }

        val locationPermissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val shouldShowRationale = locationPermissions.any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
        }

        if (shouldShowRationale) {
            showPermissionRationaleDialog(locationPermissions)
        } else {
            requestPermissions(locationPermissions)
        }
    }

    private fun getPermissionsToRequest(): List<String> {
        val permissions = mutableListOf<String>()
        if (!hasLocationPermission()) {
            permissions.addAll(
                listOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        return permissions
    }

    private fun showPermissionRationaleDialog(permissions: List<String>) {
        val message = when {
            permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    permissions.contains(Manifest.permission.POST_NOTIFICATIONS) -> {
                R.string.allow_access_detail_location_and_notifications
            }
            permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                R.string.allow_access_detail_location
            }
            permissions.contains(Manifest.permission.POST_NOTIFICATIONS) -> {
                R.string.allow_access_detail_notifications
            }
            else -> R.string.allow_access_detail_location
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.allow_access)
            .setMessage(message)
            .setPositiveButton(R.string.ok_title) { dialog, _ ->
                dialog.dismiss()
                requestPermissions(permissions)
            }
            .setNegativeButton(R.string.cancel_title) { dialog, _ ->
                dialog.dismiss()
                // Prompt user to enter a default city or location name
            }
            .setCancelable(false) // Prevent dismissal by tapping outside
            .show()
    }

    private fun requestPermissions(permissions: List<String>) {
        permissionLauncher.launch(permissions.toTypedArray())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        checkAllPermissions()
        setContent {
            WeatherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}