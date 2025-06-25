package com.example.pehelper.presentation.screen

import android.util.Log
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
        val currentState = state
        if (currentState is AuthState.Success) {
            val route = when (currentState.role) {
                "Student" -> "student_profile"
                "Curator" -> "curator_profile"
                "Teacher" -> "profile"
                "SportsOrganizer" -> "sports_organizer_profile"
                else -> "auth"
            }
            Log.d(
                "SplashScreen",
                "State is Success. Role: ${currentState.role}. Navigating to: $route"
            )
            navController.navigate(route) {
                popUpTo("splash") { inclusive = true }
            }
        } else if (currentState is AuthState.Error) {
            Log.d("SplashScreen", "State is Error. Navigating to auth.")
            navController.navigate("auth") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
} 