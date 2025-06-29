package com.example.pehelper.presentation.screen

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pehelper.R
import com.example.pehelper.data.model.CuratorStudentProfileResponse
import com.example.pehelper.presentation.component.AppButton
import com.example.pehelper.presentation.component.AvatarPicker
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CuratorStudentProfileScreen(
    studentId: String,
    onBack: () -> Unit,
    onViewAllAttendances: (String) -> Unit = {},
    viewModel: CuratorStudentProfileViewModel = koinViewModel(),
    avatarViewModel: AvatarViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val avatarState by avatarViewModel.avatarState.collectAsState()
    val avatarLoadState by avatarViewModel.avatarLoadState.collectAsState()

    LaunchedEffect(studentId) {
        viewModel.loadProfile(studentId)
    }

    LaunchedEffect(state) {
        if (state is CuratorStudentProfileState.Success) {
            (state as CuratorStudentProfileState.Success).data.student.avatarId?.let { avatarId ->
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
            is CuratorStudentProfileState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is CuratorStudentProfileState.Error -> {
                Text(
                    text = currentState.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is CuratorStudentProfileState.Success -> {
                CuratorStudentProfileContent(
                    data = currentState.data,
                    avatarViewModel = avatarViewModel,
                    onViewAllAttendances = onViewAllAttendances,
                    studentId = studentId
                )
            }
        }
        
        IconButton(
            onClick = onBack,
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
fun CuratorStudentProfileContent(
    data: CuratorStudentProfileResponse,
    avatarViewModel: AvatarViewModel,
    onViewAllAttendances: (String) -> Unit,
    studentId: String
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
                data.student.id?.let { userId ->
                    avatarViewModel.uploadAvatar(context, userId, uri, data.student.avatarId)
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
            text = stringResource(R.string.profile_student_title),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(R.string.profile_access),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        ProfileInfoBlock(label = stringResource(R.string.full_name), value = data.student.fullName)
        ProfileInfoBlock(label = stringResource(R.string.email), value = data.student.email)
        ProfileInfoBlock(label = stringResource(R.string.faculty), value = data.student.faculty?.name)
        ProfileInfoBlock(label = stringResource(R.string.course), value = data.student.course?.toString())
        ProfileInfoBlock(label = stringResource(R.string.group), value = data.student.group)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileInfoBlock(
                label = stringResource(R.string.classes_amount), 
                value = data.student.classesAmount?.toString(),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = {
                    onViewAllAttendances(studentId)
                },
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
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}