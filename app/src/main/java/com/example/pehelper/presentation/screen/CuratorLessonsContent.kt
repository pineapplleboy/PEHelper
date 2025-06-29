package com.example.pehelper.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pehelper.R
import com.example.pehelper.data.model.toLessonModel
import com.example.pehelper.presentation.component.TeacherLessonCard
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CuratorLessonsContent(
	viewModel: TeacherPairsViewModel = koinViewModel(),
	navController: NavController? = null
) {
	val pairsState by viewModel.pairsState.collectAsState()

	LaunchedEffect(Unit) {
		viewModel.getPairs()
	}

	Scaffold(
	bottomBar = {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 12.dp, start = 32.dp, end = 32.dp),
			contentAlignment = Alignment.Center
		) {
			Button(
				onClick = { navController?.navigate("create_lesson") },
				modifier = Modifier.fillMaxWidth()
			) {
				Text(
					text = "Добавить занятие",
					fontSize = 20.sp
				)
			}
		}
	}

	) { padding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding).padding(top = 32.dp)
		) {
			when (val state = pairsState) {
				is TeacherPairsState.Loading -> {
					Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
						Text(stringResource(id = R.string.loading))
					}
				}

				is TeacherPairsState.Error   -> {
					Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
						Text(stringResource(id = R.string.error_with_colon, state.error), color = MaterialTheme.colorScheme.error)
					}
				}

				is TeacherPairsState.Success -> {
					if (state.pairs.isEmpty()) {
						Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
							Text(stringResource(id = R.string.no_lessons))
						}
					} else {
						LazyColumn(
							modifier = Modifier
								.fillMaxSize()
								.padding(top = 8.dp, bottom = 16.dp),
							verticalArrangement = Arrangement.spacedBy(16.dp),
							contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
						) {
							items(state.pairs) { pair ->
								TeacherLessonCard(lesson = pair.toLessonModel(),
									onClick = {
										navController?.navigate("lesson_students/${pair.id}/${pair.toLessonModel().time}/${pair.toLessonModel().title}")
									})
							}
						}
					}
				}
			}
		}
	}
}