package com.example.pehelper.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pehelper.R
import com.example.pehelper.data.model.CuratorProfileModel
import com.example.pehelper.data.model.Faculty
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
		Box(
			modifier = Modifier.fillMaxSize()
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

				is ProfileState.SuccessTeacher -> {
					ProfileContent(
						profile = currentState.profile,
						onLogout = {
							authViewModel.logout()
							navController.navigate("auth") {
								popUpTo("profile") { inclusive = true }
							}
						},
						onBack = {
							navController.navigate("sport_lessons") {
								popUpTo("profile") { inclusive = true }
							}
						}
					)
				}

				is ProfileState.SuccessStudent -> {}

				else -> {}
			}
			IconButton(
				onClick = { navController.popBackStack() },
				modifier = Modifier.align(Alignment.TopStart).padding(start = 24.dp, top = 23.dp).size(48.dp)
			) {
				Icon(
					painter = painterResource(id = R.drawable.back_arrow_ic),
					contentDescription = stringResource(id = R.string.back),
					tint = colorResource(id = R.color.red)
				)
			}
		}
	}
}

@Composable
fun ProfileContent(profile: TeacherProfileModel, onLogout: () -> Unit, onBack: () -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(Color.White)
			.padding(horizontal = 24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Top
	) {
		TitleField(
			loginText = stringResource(R.string.profile_teacher_title),
			accessText = stringResource(R.string.profile_teacher_access),
			modifier = Modifier.padding(top = 0.dp, bottom = 0.dp)
		)
		Spacer(modifier = Modifier.height(32.dp))
		ProfileInfoBlock(label = stringResource(R.string.full_name), value = profile.fullName)
		ProfileInfoBlock(label = stringResource(R.string.email), value = profile.email)
		if (!profile.subjects.isNullOrEmpty()) {
			SubjectsBlock(subjects = profile.subjects)
		}
		Spacer(modifier = Modifier.height(32.dp))
		AppButton(text = stringResource(R.string.logout), onClick = onLogout)
	}
}

@Composable
fun ProfileInfoBlock(label: String, value: String?, modifier: Modifier = Modifier) {
	Box(
		modifier = modifier
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
				text = value ?: stringResource(R.string.not_specified),
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
				text = stringResource(R.string.subjects),
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

@Composable
fun CuratorProfileScreen(
    navController: NavController,
    onBack: () -> Unit,
    authViewModel: AuthViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val state by profileViewModel.profileState.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.getCuratorProfile()
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

            is ProfileState.SuccessCurator -> {
                CuratorProfileContent(
                    profile = currentState.profile,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate("auth") {
                            popUpTo("curator_profile") { inclusive = true }
                        }
                    }
                )
            }

            else -> {}
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
fun CuratorProfileContent(profile: CuratorProfileModel, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        TitleField(
            loginText = stringResource(R.string.profile_curator_title),
            accessText = stringResource(R.string.profile_curator_access),
            modifier = Modifier.padding(top = 0.dp, bottom = 0.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        ProfileInfoBlock(label = stringResource(R.string.full_name), value = profile.fullName)
        ProfileInfoBlock(label = stringResource(R.string.email), value = profile.email)
        if (!profile.subjects.isNullOrEmpty()) {
            SubjectsBlock(subjects = profile.subjects)
        }
        if (!profile.faculties.isNullOrEmpty()) {
            FacultiesBlock(faculties = profile.faculties)
        }
        Spacer(modifier = Modifier.height(32.dp))
        AppButton(text = stringResource(R.string.logout), onClick = onLogout)
    }
}

@Composable
fun FacultiesBlock(faculties: List<Faculty>) {
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
                text = stringResource(R.string.faculties),
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.gray)
            )
            Spacer(modifier = Modifier.height(4.dp))
            faculties.forEach {
                Text(
                    text = it.name ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorResource(R.color.light_black)
                )
            }
        }
    }
} 