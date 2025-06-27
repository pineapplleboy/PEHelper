package com.example.pehelper.presentation.di

import com.example.pehelper.presentation.screen.AuthViewModel
import com.example.pehelper.presentation.screen.ProfileViewModel
import com.example.pehelper.presentation.screen.SportsEventsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AuthViewModel() }
    viewModel { ProfileViewModel() }
    viewModel { SportsEventsViewModel() }
} 