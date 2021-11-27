package com.example.sentiance.ui.usecase

import com.example.sentiance.ui.repo.LocationRepository

class StopLocationUpdatesUseCase(private val locationRepository: LocationRepository) {
    operator fun invoke() = locationRepository.stopLocationUpdates()
}
