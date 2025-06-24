package com.example.pehelper

import android.app.Application
import com.example.pehelper.data.network.networkModule
import com.example.pehelper.presentation.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PEHelperApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PEHelperApp)
            modules(networkModule, viewModelModule)
        }
    }
} 