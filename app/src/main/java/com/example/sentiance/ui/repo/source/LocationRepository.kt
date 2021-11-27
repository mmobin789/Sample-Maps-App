package com.example.sentiance.ui.repo.source

import com.example.sentiance.sdk.LocationProviderSDK
import com.google.android.gms.maps.model.LatLng

class LocationRepository(
    private val nativeGpsLocationProvider: LocationProviderSDK,
    private val nativeNetworkLocationProvider: LocationProviderSDK
) {

    fun getCurrentLocationUpdatesPrecise(locationCallBack: (LatLng) -> Unit) =
        nativeNetworkLocationProvider.getCurrentLocationUpdates(locationCallBack)

    fun getCurrentLocationUpdatesApproximate(locationCallBack: (LatLng) -> Unit) =
        nativeGpsLocationProvider.getCurrentLocationUpdates(locationCallBack)
}