package com.example.sentiance.ui.states

import com.google.android.gms.maps.model.LatLng

sealed class UserLocationState {
    class Success(val userLocation: LatLng) : UserLocationState()
    class Error(val error: String?) : UserLocationState()
}
