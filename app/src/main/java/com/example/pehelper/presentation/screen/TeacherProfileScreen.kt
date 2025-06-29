package com.example.pehelper.presentation.screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pehelper.R
import com.example.pehelper.data.model.TeacherProfileModel
import com.example.pehelper.presentation.component.AppButton
import com.example.pehelper.presentation.component.AvatarPicker
import com.example.pehelper.presentation.screen.ProfileViewModel
import com.example.pehelper.presentation.screen.ProfileState
import org.koin.androidx.compose.koinViewModel

@Composable
fun TeacherProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = koinViewModel(),
    avatarViewModel: AvatarViewModel = koinViewModel()
) {
    val state by profileViewModel.profileState.collectAsState()
    val avatarState by avatarViewModel.avatarState.collectAsState()
    val avatarLoadState by avatarViewModel.avatarLoadState.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.getTeacherProfile()
    }

    LaunchedEffect(state) {
        if (state is ProfileState.SuccessTeacher) {
            (state as ProfileState.SuccessTeacher).profile.avatarId?.let { avatarId ->
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
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is ProfileState.Error -> {
                Text(
                    text = currentState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is ProfileState.SuccessTeacher -> {
                TeacherProfileContent(
                    profile = currentState.profile,
                    avatarViewModel = avatarViewModel,
                    profileViewModel = profileViewModel
                )
            }
            else -> {}
        }
    }
}

@Composable
fun TeacherProfileContent(
    profile: TeacherProfileModel,
    avatarViewModel: AvatarViewModel,
    profileViewModel: ProfileViewModel
) {
    var selectedAvatarUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val avatarState by avatarViewModel.avatarState.collectAsState()
    val avatarLoadState by avatarViewModel.avatarLoadState.collectAsState()

    val displayAvatarUri = when (avatarLoadState) {
        is AvatarLoadState.Success -> (avatarLoadState as AvatarLoadState.Success).avatarUri ?: selectedAvatarUri
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
                    avatarViewModel.uploadAvatar(
                        context = context, 
                        userId = userId, 
                        imageUri = uri, 
                        avatarId = profile.avatarId,
                        onSuccess = {
                            profileViewModel.getTeacherProfile()
                        }
                    )
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
            text = stringResource(R.string.profile_teacher_title),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(R.string.profile_access),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        ProfileInfoBlock(label = stringResource(R.string.full_name), value = profile.fullName ?: "")
        ProfileInfoBlock(label = stringResource(R.string.email), value = profile.email ?: "")
        ProfileInfoBlock(label = stringResource(R.string.role), value = profile.role ?: "")
        
        if (!profile.subjects.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.subjects),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            profile.subjects.forEach { subject ->
                ProfileInfoBlock(
                    label = stringResource(R.string.subject),
                    value = subject.name ?: ""
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ProfileInfoBlock(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(R.color.light_gray))
            .padding(16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
} 