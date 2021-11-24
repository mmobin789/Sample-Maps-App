package com.example.sentiance

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sentiance.permission.location.LocationUtils.onLocationPermissionResult
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment

class MainActivity : AppCompatActivity() {

    private lateinit var googleMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMap()
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it

            //   if (checkLocationPermission()) {
            setMapLocationListener()
            //  }
        }
    }

    private fun setMapLocationListener() {
        // todo here
        googleMap.setOnCameraMoveListener {
            val latlng = googleMap.cameraPosition.target
            Toast.makeText(
                this,
                latlng.latitude.toString() + "" + latlng.longitude,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        onLocationPermissionResult(requestCode, permissions, grantResults) { granted ->
            if (granted) {
                setMapLocationListener()
            } else {
                // todo show error
            }
        }
    }
}
