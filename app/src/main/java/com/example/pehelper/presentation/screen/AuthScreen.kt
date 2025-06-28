package com.example.pehelper.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pehelper.R
import com.example.pehelper.presentation.component.AppButton
import com.example.pehelper.presentation.component.InputField
import com.example.pehelper.presentation.component.TitleField
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        verticalArrangement = Arrangement.Center
    ) {
        TitleField(
            loginText = stringResource(R.string.login),
            accessText = stringResource(R.string.enter_for_access)
        )
        InputField(
            hint = stringResource(R.string.email),
            iconId = R.drawable.ic_email,
            value = email,
            modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp)
        ) {
            email = it
        }
        InputField(
            hint = stringResource(R.string.password),
            iconId = R.drawable.ic_eye,
            value = password,
            modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp),
            isPasswordField = true
        ) {
            password = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppButton(text = stringResource(R.string.next), onClick = {
            viewModel.login(email, password)
        })
        Spacer(modifier = Modifier.height(16.dp))
        when (val currentState = state) {
            is AuthState.Loading -> CircularProgressIndicator()
            is AuthState.Error -> Text(
                currentState.error,
                color = MaterialTheme.colorScheme.error
            )

            is AuthState.Success -> {
                LaunchedEffect(currentState) {
                    when (currentState.role) {
                        "Student" -> navController.navigate("student_profile") {
                            popUpTo("auth") { inclusive = true }
                        }

                        "Curator" -> navController.navigate("curator_profile") {
                            popUpTo("auth") { inclusive = true }
                        }

                        "Teacher" -> navController.navigate("profile") {
                            popUpTo("auth") { inclusive = true }
                        }

                        "SportsOrganizer" -> navController.navigate("sports_events") {
                            popUpTo("auth") { inclusive = true }
                        }

                        else -> {
                            navController.navigate("auth") {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    }
                }
            }

            else -> {}
        }
    }
} 