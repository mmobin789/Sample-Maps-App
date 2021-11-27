package com.example.sentiance.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sentiance.ui.states.UserLocationState
import com.example.sentiance.ui.usecase.GetLocationInGeoFenceUseCase
import com.example.sentiance.ui.usecase.StopLocationUpdatesUseCase

class MainActivityViewModel(
    private val getLocationInGeoFenceUseCase: GetLocationInGeoFenceUseCase,
    private val stopLocationUpdatesUseCase: StopLocationUpdatesUseCase
) :
    ViewModel() {

    private val state = MutableLiveData<UserLocationState>()

    fun state(): LiveData<UserLocationState> = state

    fun getCurrentLocationUpdates(lowBattery: Boolean) {

        getLocationInGeoFenceUseCase({
            state.value = UserLocationState.Success(it)
        }, {
            state.value = UserLocationState.Error(it)
        }, lowBattery)
    }

    fun stopLocationUpdates() = stopLocationUpdatesUseCase()
}
