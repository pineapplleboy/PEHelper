package com.example.pehelper.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pehelper.R
import com.example.pehelper.data.model.Subject
import com.example.pehelper.data.model.TeacherProfileModel
import com.example.pehelper.presentation.component.AppButton
import com.example.pehelper.presentation.component.TitleField
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    val state by profileViewModel.profileState.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.getTeacherProfile()
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
            is ProfileState.Success -> {
                ProfileContent(
                    profile = currentState.profile,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate("auth") {
                            popUpTo("profile") { inclusive = true }
                        }
                    }
                )
            }
            else -> {}
        }
    }
}

@Composable
fun ProfileContent(profile: TeacherProfileModel, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TitleField(
            loginText = "Профиль преподавателя",
            accessText = "Информация о вашей учётной записи",
            modifier = Modifier.padding(top = 0.dp, bottom = 0.dp).height(64.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        ProfileInfoBlock(label = "ФИО", value = profile.fullName)
        ProfileInfoBlock(label = "Email", value = profile.email)
        if (!profile.subjects.isNullOrEmpty()) {
            SubjectsBlock(subjects = profile.subjects)
        }
        Spacer(modifier = Modifier.height(32.dp))
        AppButton(text = "Выйти", onClick = onLogout)
    }
}

@Composable
fun ProfileInfoBlock(label: String, value: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(R.color.light_gray))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.gray)
            )
            Text(
                text = value ?: "Не указано",
                style = MaterialTheme.typography.bodyLarge,
                color = colorResource(R.color.light_black)
            )
        }
    }
}

@Composable
fun SubjectsBlock(subjects: List<Subject>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(R.color.light_gray))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Предметы:",
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.gray)
            )
            Spacer(modifier = Modifier.height(4.dp))
            subjects.forEach {
                Text(
                    text = it.name ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorResource(R.color.light_black)
                )
            }
        }
    }
} 