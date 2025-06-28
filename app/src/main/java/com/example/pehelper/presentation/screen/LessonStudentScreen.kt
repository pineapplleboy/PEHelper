package com.example.pehelper.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pehelper.R
import com.example.pehelper.presentation.component.StudentCard

@Composable
fun LessonStudentsScreen(
	lessonId: String,
	lessonTime: String,
	lessonTitle: String,
	navController: NavController? = null
) {
	val isSearching = remember { mutableStateOf(false) }
	val searchText = remember { mutableStateOf("") }
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
	){ padding ->
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.padding(horizontal = 16.dp),
			verticalArrangement = Arrangement.Top
		) {
			item{
				StudentCard(
					fullName = "Dr. Raul Zirkind",
					onAccept = {},
					onDecline = {}
				)
			}
			item{
				Spacer(modifier = Modifier.padding(8.dp))
			}
			item{
				StudentCard(
					fullName = "BACHAEV MARAT VITALIEVICH",
					onAccept = {},
					onDecline = {}
				)
			}

		}
	}
}