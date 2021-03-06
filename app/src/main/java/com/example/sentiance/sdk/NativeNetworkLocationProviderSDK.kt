package com.example.sentiance.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import com.example.sentiance.R
import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.TimeUnit

/**
 * Uses GPS and network to query precise device location using android api's
 */
class NativeNetworkLocationProviderSDK(private val context: Context) : LocationProviderSDK {
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private lateinit var locationListener: LocationListener

    private var locationAge = 0L

    @SuppressLint("MissingPermission")
    override fun getCurrentLocationUpdates(
        locationCallBack: (LatLng) -> Unit,
        errorCallback: (String) -> Unit
    ) {

        if (locationManager.isProviderEnabled(NETWORK_PROVIDER).not()) {
            errorCallback(context.getString(R.string.err_no_network_only_gps))
        }

        if (locationManager.isProviderEnabled(GPS_PROVIDER).not()) {
            errorCallback(context.getString(R.string.err_no_gps))
            return
        }

        val isCachedLocationOld =
            System.currentTimeMillis() - locationAge >= TimeUnit.MINUTES.toMillis(5)

        if (isCachedLocationOld.not()) { // no need to check cached location if old.
            val cachedLocation = locationManager.getLastKnownLocation(NETWORK_PROVIDER)

            if (cachedLocation != null) {
                locationCallBack(LatLng(cachedLocation.latitude, cachedLocation.longitude))
                return
            }
        }

        locationListener = LocationListener {
            locationCallBack(LatLng(it.latitude, it.longitude))
        }

        locationManager.requestLocationUpdates(
            NETWORK_PROVIDER,
            TimeUnit.SECONDS.toMillis(10), // Same as below
            0f, // meters and this distance been set to 0 for easier testing purposes.
            locationListener
        )

        locationAge = System.currentTimeMillis()
    }

    override fun stopLocationUpdates() {
        if (::locationListener.isInitialized)
            locationManager.removeUpdates(locationListener)
    }
}
