package com.example.sentiance.ui.repo

import com.example.sentiance.sdk.LocationProviderSDK
import com.google.android.gms.maps.model.LatLng

class LocationRepository(
    private val nativeGpsLocationProvider: LocationProviderSDK,
    private val nativeNetworkLocationProvider: LocationProviderSDK
) {

    fun getCurrentLocationUpdatesPrecise(
        locationCallBack: (LatLng) -> Unit,
        errorCallback: (String) -> Unit
    ) =
        nativeNetworkLocationProvider.getCurrentLocationUpdates(locationCallBack, errorCallback)

    fun getCurrentLocationUpdatesApproximate(
        locationCallBack: (LatLng) -> Unit,
        errorCallback: (String) -> Unit
    ) =
        nativeGpsLocationProvider.getCurrentLocationUpdates(locationCallBack, errorCallback)

    fun stopLocationUpdates() {
        nativeGpsLocationProvider.stopLocationUpdates()
        nativeNetworkLocationProvider.stopLocationUpdates()
    }
}
