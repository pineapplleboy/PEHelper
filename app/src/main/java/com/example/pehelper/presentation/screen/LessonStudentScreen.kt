package com.example.pehelper.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pehelper.R
import com.example.pehelper.presentation.component.StudentCard
import com.example.pehelper.presentation.viewmodel.LessonStudentsViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LessonStudentsScreen(
	lessonId: String,
	lessonTime: String,
	lessonTitle: String,
	navController: NavController? = null
) {
	val viewModel: LessonStudentsViewModel = koinViewModel { parametersOf(lessonId) }
	val avatarViewModel: AvatarViewModel = koinViewModel()
	val pending by viewModel.pending.collectAsState()
	val solved by viewModel.solved.collectAsState()
	val isSearching = remember { mutableStateOf(false) }
	val searchText = remember { mutableStateOf("") }
	val selectedTab = remember { mutableStateOf(0) }

	Scaffold(
		topBar = {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				IconButton(onClick = { navController?.popBackStack() }) {
					Icon(
						painter = painterResource(id = R.drawable.back_arrow_ic),
						contentDescription = "Назад"
					)
				}
				Spacer(modifier = Modifier.width(8.dp))
				if (!isSearching.value) {
					Text(
						text = "$lessonTitle $lessonTime",
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.Bold,
						fontSize = 20.sp,
						modifier = Modifier.weight(1f)
					)
					IconButton(onClick = { isSearching.value = true }) {
						Icon(
							painter = painterResource(id = R.drawable.search_ic),
							contentDescription = "Поиск"
						)
					}
				} else {
					TextField(
						value = searchText.value,
						onValueChange = { searchText.value = it },
						placeholder = { Text("Поиск...") },
						modifier = Modifier.weight(1f)
					)
					IconButton(onClick = {
						isSearching.value = false
						searchText.value = ""
					}) {
						Icon(
							painter = painterResource(id = R.drawable.cross_search_ic),
							contentDescription = "Закрыть"
						)
					}
				}
			}
		}
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 32.dp),
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Column(
					modifier = Modifier
						.weight(1f)
						.clickable { selectedTab.value = 0 },
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = "В ожидании",
						color = if (selectedTab.value == 0) Color.Black else Color.Black.copy(alpha = 0.4f),
						fontWeight = if (selectedTab.value == 0) FontWeight.Bold else FontWeight.Normal
					)
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.height(2.dp)
							.background(if (selectedTab.value == 0) Color.Black else Color.Transparent)
					)
				}
				Column(
					modifier = Modifier
						.weight(1f)
						.clickable { selectedTab.value = 1 },
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = "Обработанные",
						color = if (selectedTab.value == 1) Color.Black else Color.Black.copy(alpha = 0.4f),
						fontWeight = if (selectedTab.value == 1) FontWeight.Bold else FontWeight.Normal
					)
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.height(2.dp)
							.background(if (selectedTab.value == 1) Color.Black else Color.Transparent)
					)
				}
			}
			Spacer(modifier = Modifier.height(8.dp))
			val students = if (selectedTab.value == 0) pending else solved
			val filtered = if (searchText.value.isBlank()) students else students.filter { it.student?.name?.contains(searchText.value, ignoreCase = true) == true }
			LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.padding(horizontal = 16.dp),
				verticalArrangement = Arrangement.Top
			) {
				items(filtered) { attendance ->
					attendance.student?.name?.let {
						StudentCard(
							attendance.student,
							attendance.status.toString(),
							onAccept = { amount -> viewModel.acceptAttendance(attendance.student?.id ?: "", amount) },
							onDecline = { viewModel.declineAttendance(attendance.student?.id ?: "") },
							viewModel
						)
					}
				}
			}
		}
	}
}

@Composable
@Preview
fun LessonStudentsScreenPreview(){
	LessonStudentsScreen("fdg",":dfg","gfdg")
}