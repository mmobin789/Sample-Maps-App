package com.example.sentiance.di

import android.content.Context
import com.example.sentiance.BuildConfig
import com.example.sentiance.sdk.NativeGpsLocationProviderSDK
import com.example.sentiance.sdk.NativeNetworkLocationProviderSDK
import com.example.sentiance.ui.MainActivityViewModel
import com.example.sentiance.ui.repo.LocationRepository
import com.example.sentiance.ui.usecase.GetLocationInGeoFenceUseCase
import com.example.sentiance.ui.usecase.StopLocationUpdatesUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

/**
 * The dependency injection service specific to currency exchange feature.
 */
object DI {
    private var init = false

    /**
     * This initializes Koin DI for the whole application.
     * This must be only called once.
     */
    fun start(context: Context) {

        if (init)
            return

        val uiModule = module {
            factory { NativeNetworkLocationProviderSDK(androidContext()) }
            factory { NativeGpsLocationProviderSDK(androidContext()) }
            factory {
                LocationRepository(
                    get<NativeGpsLocationProviderSDK>(),
                    get<NativeNetworkLocationProviderSDK>()
                )
            }
            factory { GetLocationInGeoFenceUseCase(get()) }
            factory { StopLocationUpdatesUseCase(get()) }
            viewModel { MainActivityViewModel(get(), get()) }
        }

        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(context)
            modules(uiModule)
        }

        init = true
    }
}
