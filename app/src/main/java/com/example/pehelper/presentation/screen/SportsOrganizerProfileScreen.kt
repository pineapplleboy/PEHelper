package com.example.pehelper.presentation.screen

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pehelper.R
import com.example.pehelper.data.model.StudentProfileModel
import com.example.pehelper.presentation.component.AppButton
import com.example.pehelper.presentation.component.AvatarPicker
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SportsOrganizerProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel(),
    avatarViewModel: AvatarViewModel = koinViewModel()
) {
    val state by profileViewModel.profileState.collectAsState()
    val avatarState by avatarViewModel.avatarState.collectAsState()
    val avatarLoadState by avatarViewModel.avatarLoadState.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.getStudentProfile()
    }

    LaunchedEffect(state) {
        if (state is ProfileState.SuccessStudent) {
            (state as ProfileState.SuccessStudent).profile.avatarId?.let { avatarId ->
                avatarViewModel.loadAvatar(avatarId)
            }
        }
    }

    LaunchedEffect(avatarState) {
        if (avatarState is AvatarState.Success) {
            avatarViewModel.resetAvatarState()
        }
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
                    navController = navController,
                    avatarViewModel = avatarViewModel
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SportsOrganizerProfileContent(
    profile: StudentProfileModel,
    onLogout: () -> Unit,
    navController: NavController,
    avatarViewModel: AvatarViewModel
) {
    var selectedAvatarUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val avatarState by avatarViewModel.avatarState.collectAsState()
    val avatarLoadState by avatarViewModel.avatarLoadState.collectAsState()

    val displayAvatarUri = when (avatarLoadState) {
        is AvatarLoadState.Success -> (avatarLoadState as AvatarLoadState.Success).avatarUri
            ?: selectedAvatarUri

        else -> selectedAvatarUri
    }

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

        AvatarPicker(
            avatarUri = displayAvatarUri,
            onAvatarSelected = { uri ->
                selectedAvatarUri = uri
                profile.id?.let { userId ->
                    avatarViewModel.uploadAvatar(context, userId, uri, profile.avatarId)
                }
            }
        )

        when {
            avatarLoadState is AvatarLoadState.Loading -> {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }

            avatarState is AvatarState.Loading -> {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }

            avatarState is AvatarState.Error -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (avatarState as AvatarState.Error).error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            avatarLoadState is AvatarLoadState.Error -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (avatarLoadState as AvatarLoadState.Error).error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            else -> {}
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
        ProfileInfoBlock(
            label = stringResource(R.string.course),
            value = profile.course?.toString()
        )
        ProfileInfoBlock(label = stringResource(R.string.group), value = profile.group)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileInfoBlock(
                label = stringResource(R.string.classes_amount),
                value = profile.classesAmount?.toString(),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = { navController.navigate("all_attendances") },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.info_ic),
                    contentDescription = stringResource(R.string.view_all_attendances),
                    modifier = Modifier.size(24.dp),
                    tint = colorResource(R.color.red)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        AppButton(text = stringResource(R.string.logout), onClick = onLogout)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatAppointmentDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) {
        return "Не указана"
    }

    return try {
        val utcDateTime = LocalDateTime.parse(dateString.replace("Z", ""))
        val utcZonedDateTime = utcDateTime.atZone(java.time.ZoneOffset.UTC)
        val localZonedDateTime = utcZonedDateTime.withZoneSameInstant(java.time.ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        localZonedDateTime.format(formatter)
    } catch (e: Exception) {
        dateString
    }
} 