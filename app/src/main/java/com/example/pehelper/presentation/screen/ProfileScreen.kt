package com.example.pehelper.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = koinViewModel()) {
    val state by viewModel.profileState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { viewModel.getStudentProfile() }) {
                Text("Student")
            }
            Button(onClick = { viewModel.getTeacherProfile() }) {
                Text("Teacher")
            }
            Button(onClick = { viewModel.getCuratorProfile() }) {
                Text("Curator")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (state) {
            is ProfileState.Loading -> CircularProgressIndicator()
            is ProfileState.Error -> Text((state as ProfileState.Error).error, color = MaterialTheme.colorScheme.error)
            is ProfileState.Success -> Text("Success!", color = MaterialTheme.colorScheme.primary)
            else -> {}
        }
    }
} 