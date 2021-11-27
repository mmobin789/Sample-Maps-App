package com.example.sentiance.ui.usecase

import com.example.sentiance.ui.repo.source.LocationRepository
import com.google.android.gms.maps.model.LatLng

class GetLocationInGeoFenceUseCase(private val locationRepository: LocationRepository) {
    operator fun invoke(locationCallBack: (LatLng) -> Unit) {

    }
}