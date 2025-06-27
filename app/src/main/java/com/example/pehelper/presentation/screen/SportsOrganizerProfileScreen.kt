package com.example.pehelper.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pehelper.R
import com.example.pehelper.data.model.StudentProfileModel
import com.example.pehelper.presentation.component.AppButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun SportsOrganizerProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val state by profileViewModel.profileState.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.getStudentProfile()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (val currentState = state) {
            is ProfileState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is ProfileState.Error -> {
                Text(
                    text = currentState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is ProfileState.SuccessStudent -> {
                SportsOrganizerProfileContent(
                    profile = currentState.profile,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate("auth") {
                            popUpTo("sports_organizer_profile") { inclusive = true }
                        }
                    },
                    navController = navController
                )
            }

            else -> {}
        }
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_arrow_ic),
                contentDescription = stringResource(id = R.string.back),
                tint = colorResource(id = R.color.black),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun SportsOrganizerProfileContent(profile: StudentProfileModel, onLogout: () -> Unit, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        // Аватарка-заглушка
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.account_circle),
                contentDescription = "Avatar",
                modifier = Modifier.size(72.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.profile_organizer_title),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(R.string.profile_access),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))
        ProfileInfoBlock(label = stringResource(R.string.full_name), value = profile.fullName)
        ProfileInfoBlock(label = stringResource(R.string.email), value = profile.email)
        ProfileInfoBlock(label = stringResource(R.string.faculty), value = profile.faculty?.name)
        ProfileInfoBlock(label = stringResource(R.string.course), value = profile.course?.toString())
        ProfileInfoBlock(label = stringResource(R.string.group), value = profile.group)
        ProfileInfoBlock(label = stringResource(R.string.classes_amount), value = profile.classesAmount?.toString())
        ProfileInfoBlock(label = stringResource(R.string.appointment_date), value = profile.appointmentDate)
        Spacer(modifier = Modifier.height(32.dp))
        Spacer(modifier = Modifier.height(16.dp))
        AppButton(text = stringResource(R.string.logout), onClick = onLogout)
    }
} 