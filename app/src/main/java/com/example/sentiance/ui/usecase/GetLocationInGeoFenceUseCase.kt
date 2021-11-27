package com.example.sentiance.ui.usecase

import com.example.sentiance.ui.repo.LocationRepository
import com.google.android.gms.maps.model.LatLng

class GetLocationInGeoFenceUseCase(private val locationRepository: LocationRepository) {
    operator fun invoke(
        locationCallBack: (LatLng) -> Unit,
        errorCallback: (String) -> Unit,
        lowBattery: Boolean
    ) {
        locationRepository.getCurrentLocationUpdatesPrecise(locationCallBack, errorCallback)
    }
}
