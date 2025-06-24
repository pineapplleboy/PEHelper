package com.example.pehelper.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkAndRefreshTokens()
    }

    LaunchedEffect(state) {
        when (state) {
            is AuthState.Success -> {
                navController.navigate("profile") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                navController.navigate("auth") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
} 