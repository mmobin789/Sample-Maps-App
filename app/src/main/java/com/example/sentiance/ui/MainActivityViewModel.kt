package com.example.sentiance.ui

import androidx.lifecycle.ViewModel
import com.example.sentiance.ui.usecase.GetLocationInGeoFenceUseCase

class MainActivityViewModel(private val getLocationInGeoFenceUseCase: GetLocationInGeoFenceUseCase) : ViewModel() {
    fun createGeoFence(radiusInput: String) {
    }
}
