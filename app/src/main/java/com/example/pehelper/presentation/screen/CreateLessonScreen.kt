package com.example.pehelper.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pehelper.R
import com.example.pehelper.data.model.CreateSportsEventRequest
import org.koin.androidx.compose.koinViewModel
import com.example.pehelper.data.model.Subject
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateLessonScreen(
	onCreated: () -> Unit,
	onBack: () -> Unit,
	viewModel: TeacherPairsViewModel = koinViewModel()
) {
	val subjectsState by viewModel.subjectsState.collectAsState()
	val createState by viewModel.createPairState.collectAsState()
	val showDialog = remember { mutableStateOf(false) }
	val selectedSubject = remember { mutableStateOf<Subject?>(null) }

	LaunchedEffect(Unit) {
		viewModel.getTeacherSubjects()
	}

	LaunchedEffect(createState) {
		if (createState is CreatePairState.Success) {
			onCreated()
		}
	}

	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(
						text = "Новое занятие",
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Bold,
						fontSize = 18.sp,
						color = Color.Black
					)
				},
				windowInsets = WindowInsets(0),
				navigationIcon = {
					TextButton(onClick = onBack) {
						Text("Отмена", color = colorResource(R.color.red), fontSize = 16.sp)
					}
				},
				actions = {
					TextButton(
						onClick = {
							selectedSubject.value?.id?.let { viewModel.createPair(it) }
						}
					) {
						Text(
							if (createState is CreatePairState.Loading) "Создание..." else "Готово",
							color = colorResource(R.color.red),
							fontSize = 16.sp
						)
					}
				},
				colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
			)
		},
		containerColor = Color.White
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.padding(horizontal = 24.dp),
			verticalArrangement = Arrangement.Top,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Spacer(modifier = Modifier.padding(16.dp))
			when (subjectsState) {
				is TeacherSubjectsState.Loading -> {
					Text("Загрузка предметов...")
				}
				is TeacherSubjectsState.Error -> {
					Text((subjectsState as TeacherSubjectsState.Error).error, color = Color.Red)
				}
				is TeacherSubjectsState.Success -> {
					val subjects = (subjectsState as TeacherSubjectsState.Success).subjects
					if (selectedSubject.value == null && subjects.isNotEmpty()) {
						selectedSubject.value = subjects.first()
					}
					Surface(
						shape = RoundedCornerShape(10.dp),
						color = Color(0xFFF5F5F5),
						modifier = Modifier
							.fillMaxWidth()
							.clickable { showDialog.value = true }
					) {
						Text(
							text = selectedSubject.value?.name ?: "Выберите предмет",
							modifier = Modifier.padding(18.dp),
							fontSize = 16.sp,
							color = Color.Black
						)
					}
					Spacer(modifier = Modifier.padding(16.dp))
				}
			}
			if (showDialog.value && subjectsState is TeacherSubjectsState.Success) {
				val subjects = (subjectsState as TeacherSubjectsState.Success).subjects
				AlertDialog(
					onDismissRequest = { showDialog.value = false },
					confirmButton = {},
					title = { Text("Выберите предмет") },
					text = {
						Column {
							subjects.forEach { subject ->
								OutlinedButton(
									onClick = {
										selectedSubject.value = subject
										showDialog.value = false
									},
									modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
								) {
									Text(subject.name ?: "")
								}
							}
						}
					}
				)
			}
			if (createState is CreatePairState.Error) {
				Text((createState as CreatePairState.Error).error, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
			}
		}
	}
}