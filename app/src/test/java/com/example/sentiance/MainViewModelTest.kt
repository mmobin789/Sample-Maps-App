package com.example.sentiance

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.sentiance.ui.MainActivityViewModel
import com.example.sentiance.ui.states.UserLocationState
import com.example.sentiance.ui.usecase.GetLocationInGeoFenceUseCase
import com.example.sentiance.ui.usecase.StopLocationUpdatesUseCase
import com.google.android.gms.maps.model.LatLng
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val getLocationInGeoFenceUseCase = mockk<GetLocationInGeoFenceUseCase>()

    private val stopLocationUpdatesUseCase = mockk<StopLocationUpdatesUseCase>()

    private val viewModel =
        MainActivityViewModel(getLocationInGeoFenceUseCase, stopLocationUpdatesUseCase)

    @Test
    fun `Given user location from network location sdk using internet and gps is success`() {

        val state = viewModel.state()

        var stateObserver: Observer<UserLocationState>? = null

        stateObserver = Observer<UserLocationState> {
            if (it is UserLocationState.Success) {
                state.removeObserver(stateObserver!!)
            }
        }

        every {
            getLocationInGeoFenceUseCase(any(), any(), any())
        } answers {
            firstArg<(LatLng) -> Unit>()(mockk())
        }

        state.observeForever(stateObserver)

        viewModel.getCurrentLocationUpdates(lowBattery = false)

        assertTrue(state.value is UserLocationState.Success)
    }

    @Test
    fun `Given user location from network location sdk using internet is failed but gps is success`() {

        val state = viewModel.state()

        var state1: UserLocationState.Error? = null

        var state2: UserLocationState? = null

        val stateObserver = Observer<UserLocationState> {
            if (it is UserLocationState.Success) {
                state2 = it
            } else state1 = it as UserLocationState.Error
        }
        val capturedCallback1 = slot<((LatLng) -> Unit)>()
        // val capturedData1 = slot<LatLng>()
        val capturedCallback2 = slot<((String) -> Unit)>()
        //   val capturedData2 = slot<String>()

        val networkError = "Network is disabled location may not be precise"

        every {
            getLocationInGeoFenceUseCase(
                capture(capturedCallback1),
                capture(capturedCallback2),
                any()
            )
        } answers {
            capturedCallback2.captured(networkError)
            capturedCallback1.captured(mockk())
        }

        state.observeForever(stateObserver)

        viewModel.getCurrentLocationUpdates(lowBattery = false)

        assertNotNull(capturedCallback2.captured)

        assertTrue(state2 is UserLocationState.Success)
        assertTrue(state1?.error == networkError)

        state.removeObserver(stateObserver)
    }

    @Test
    fun `Given user location from network sdk using internet is failed 1st`() {

        val state = viewModel.state()

        var errorState: UserLocationState.Error? = null

        val stateObserver = Observer<UserLocationState> {
            if (it is UserLocationState.Error)
                errorState = it
        }

        val networkError = "Network is disabled location may not be precise"

        every {
            getLocationInGeoFenceUseCase(any(), any(), any())
        } answers {
            secondArg<((String) -> Unit)>()(networkError)
        }

        state.observeForever(stateObserver)

        viewModel.getCurrentLocationUpdates(lowBattery = false)

        assertTrue(errorState?.error == networkError)
        state.removeObserver(stateObserver)
    }

    @Test
    fun `Given user location from network sdk using gps is failed 2nd`() {

        val state = viewModel.state()

        var errorState: UserLocationState.Error? = null

        val stateObserver = Observer<UserLocationState> {
            if (it is UserLocationState.Error)
                errorState = it
        }

        val gpsError = "GPS Disabled"

        every {
            getLocationInGeoFenceUseCase(any(), any(), any())
        } answers {
            secondArg<((String) -> Unit)>()(gpsError)
        }

        state.observeForever(stateObserver)

        viewModel.getCurrentLocationUpdates(lowBattery = false)

        assertTrue(errorState?.error == gpsError)
        state.removeObserver(stateObserver)
    }

    @Test
    fun `Given user location from gps sdk using only gps is success`() {

        val state = viewModel.state()

        var stateObserver: Observer<UserLocationState>? = null

        stateObserver = Observer<UserLocationState> {
            if (it is UserLocationState.Success) {
                state.removeObserver(stateObserver!!)
            }
        }

        every {
            getLocationInGeoFenceUseCase(any(), any(), any())
        } answers {
            firstArg<(LatLng) -> Unit>()(mockk())
        }

        state.observeForever(stateObserver)

        viewModel.getCurrentLocationUpdates(lowBattery = true)

        assertTrue(state.value is UserLocationState.Success)
    }

    @Test
    fun `Given user location from gps sdk using only gps is failed`() {

        val state = viewModel.state()

        var stateObserver: Observer<UserLocationState>? = null

        stateObserver = Observer<UserLocationState> {
            if (it is UserLocationState.Success) {
                state.removeObserver(stateObserver!!)
            }
        }

        val gpsError = "GPS Disabled"

        every {
            getLocationInGeoFenceUseCase(any(), any(), any())
        } answers {
            secondArg<(String) -> Unit>()(gpsError)
        }

        state.observeForever(stateObserver)

        viewModel.getCurrentLocationUpdates(lowBattery = true)

        assertTrue((state.value as UserLocationState.Error).error == gpsError)
    }

    @Test
    fun `stop location updates from SDK`() {

        every { stopLocationUpdatesUseCase() } answers {
        }

        viewModel.stopLocationUpdates()
    }
}
