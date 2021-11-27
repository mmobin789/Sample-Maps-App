package com.example.sentiance.sdk

import com.google.android.gms.maps.model.LatLng

interface LocationProviderSDK {
    fun getCurrentLocationUpdates(
        locationCallBack: (LatLng) -> Unit,
        errorCallback: (String) -> Unit
    )

    fun stopLocationUpdates()
}
