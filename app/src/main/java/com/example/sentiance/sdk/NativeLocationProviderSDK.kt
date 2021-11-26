package com.example.sentiance.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.TimeUnit

class NativeLocationProviderSDK(context: Context) : LocationProviderSDK {
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private lateinit var locationListener: LocationListener

    private var locationAge = 0L

    @SuppressLint("MissingPermission")
    override fun getCurrentLocationUpdates(locationCallBack: (LatLng) -> Unit) {

        val isCachedLocationOld =
            System.currentTimeMillis() - locationAge >= TimeUnit.MINUTES.toMillis(5)

        if (isCachedLocationOld.not()) { // no need to check cached location if old.
            val cachedLocation = locationManager.getLastKnownLocation(GPS_PROVIDER)

            if (cachedLocation != null) {
                locationCallBack(LatLng(cachedLocation.latitude, cachedLocation.longitude))
                return
            }
        }

        locationListener = LocationListener {
            locationCallBack(LatLng(it.latitude, it.longitude))
        }

        locationManager.requestLocationUpdates(
            GPS_PROVIDER,
            TimeUnit.SECONDS.toMillis(5),
            0f, // meters
            locationListener
        )

        locationAge = System.currentTimeMillis()
    }

    override fun stopLocationUpdates() {
        if (::locationListener.isInitialized)
            locationManager.removeUpdates(locationListener)
    }
}
