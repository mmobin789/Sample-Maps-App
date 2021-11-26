package com.example.sentiance.ui

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sentiance.R
import com.example.sentiance.databinding.ActivityMainBinding
import com.example.sentiance.permission.location.hasLocationPermissions
import com.example.sentiance.permission.location.onLocationPermissionResult
import com.example.sentiance.sdk.LocationProviderSDK
import com.example.sentiance.sdk.NativeLocationProviderSDK
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions

class MainActivity : AppCompatActivity() {

    private lateinit var googleMap: GoogleMap

    private var geoFence: Circle? = null

    private var hasLocationPermissions = false

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val locationProviderSDK: LocationProviderSDK by lazy {
        NativeLocationProviderSDK(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync {
            googleMap = it
            setMapLocationListener()
        }
    }

    @SuppressLint("MissingPermission")
    private fun setMapLocationListener() {

        hasLocationPermissions = hasLocationPermissions()

        googleMap.isMyLocationEnabled = hasLocationPermissions

        googleMap.setOnCameraMoveListener(this::addCircleOnMap)
    }

    private fun getCurrentLocationUpdates() {

        if (hasLocationPermissions.not())
            return

        locationProviderSDK.getCurrentLocationUpdates { latLng ->

            geoFence?.run {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 16f))
                val results = floatArrayOf(0f) // distance b/w user's location and circle's center.
                Location.distanceBetween(
                    latLng.latitude,
                    latLng.longitude,
                    center.latitude,
                    center.longitude,
                    results
                )
                val distance = results[0]
                val message = if (distance <= radius) {
                    // inside circle
                    "Inside Circle"
                } else {
                    // outside circle
                    "Outside Circle"
                }
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun addCircleOnMap() = googleMap.run {
        geoFence?.remove()
        val circleOptions = CircleOptions()
        val selectedLocation = cameraPosition.target
        circleOptions.center(selectedLocation)

        val radius = binding.etRadius.text.toString()

        if (radius.isNotBlank()) {
            circleOptions.radius(radius.toDouble())
            geoFence = addCircle(circleOptions)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        onLocationPermissionResult(requestCode, grantResults) { granted ->

            googleMap.isMyLocationEnabled = granted

            if (granted)
                getCurrentLocationUpdates()
            else Toast.makeText(this, R.string.location_required, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        locationProviderSDK.stopLocationUpdates()
        super.onPause()
    }

    override fun onResume() {
        getCurrentLocationUpdates()
        super.onResume()
    }
}
