package com.example.sentiance.ui

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.sentiance.R
import com.example.sentiance.databinding.ActivityMainBinding
import com.example.sentiance.di.DI
import com.example.sentiance.permission.location.hasLocationPermissions
import com.example.sentiance.permission.location.onLocationPermissionResult
import com.example.sentiance.ui.states.UserLocationState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var googleMap: GoogleMap

    private var geoFence: Circle? = null

    private var hasLocationPermissions = false

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel: MainActivityViewModel by viewModel()

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        DI.start(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        hasLocationPermissions = hasLocationPermissions()

        mapFragment.getMapAsync {
            googleMap = it
            setMapLocationListener()
        }

        createNotificationChannel()
    }

    @SuppressLint("MissingPermission")
    private fun setMapLocationListener() {

        googleMap.isMyLocationEnabled = hasLocationPermissions

        googleMap.setOnCameraMoveListener(this::addCircleOnMap)
    }

    private fun getCurrentLocationUpdates() {

        val lowBattery = checkBatteryStateOptimal().not()

        if (hasLocationPermissions.not())
            return

        viewModel.getCurrentLocationUpdates(lowBattery)

        viewModel.state().observe(this) { state ->

            when (state) {
                is UserLocationState.Success -> {
                    geoFence?.run {
                        val latLng = state.userLocation

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 16f))
                        val results =
                            floatArrayOf(0f) // distance b/w user's location and circle's center.
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
                            getString(R.string.txt_user_inside_geofence)
                        } else {
                            // outside circle
                            getString(R.string.txt_user_outside_geofence)
                        }
                        showNotification(message)
                    }
                }

                is UserLocationState.Error -> {
                    state.error?.let { showNotification(it) }
                }
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

    private fun checkBatteryStateOptimal(): Boolean {
        val batteryStatus = registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        val batteryPct: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }

        return batteryPct != null && batteryPct >= 50f
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
            hasLocationPermissions = granted

            if (granted) {
                getCurrentLocationUpdates()
            } else showNotification(getString(R.string.err_location_required))
        }
    }

    override fun onPause() {
        viewModel.stopLocationUpdates()
        super.onPause()
    }

    override fun onResume() {
        getCurrentLocationUpdates()
        super.onResume()
    }

    private fun showNotification(textContent: String) {
        val builder = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("Geofence Entry/Exit")
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        notificationManager.notify(1, builder.build())


    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = "Geofence"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system

            notificationManager.createNotificationChannel(channel)
        }
    }
}
