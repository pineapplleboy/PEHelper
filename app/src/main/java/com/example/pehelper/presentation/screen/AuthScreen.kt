package com.example.pehelper.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(viewModel: AuthViewModel = koinViewModel()) {
    val state by viewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var refreshToken by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { viewModel.login(email, password) }) {
                Text("Login")
            }
            Button(onClick = { viewModel.logout() }) {
                Text("Logout")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = refreshToken,
            onValueChange = { refreshToken = it },
            label = { Text("Refresh Token") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { viewModel.refresh(refreshToken) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Refresh")
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (state) {
            is AuthState.Loading -> CircularProgressIndicator()
            is AuthState.Error -> Text((state as AuthState.Error).error, color = MaterialTheme.colorScheme.error)
            is AuthState.Success -> Text("Success!", color = MaterialTheme.colorScheme.primary)
            else -> {}
        }
    }
} 