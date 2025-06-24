package com.example.pehelper.presentation.di

import com.example.pehelper.data.repository.TokenStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {
    single { TokenStorage(androidContext()) }
} 