package com.example.pehelper.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.pehelper.presentation.component.StudentLessonCard
import com.example.pehelper.presentation.component.StudentEventCard
import org.koin.androidx.compose.koinViewModel

enum class StudentViewType {
	PAIRS,
	EVENTS
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudentPairsScreen(
	onProfileClick: () -> Unit = {},
	onEventClick: (String) -> Unit = {},
	viewModel: StudentPairsViewModel = koinViewModel(),
	navController: NavController? = null
) {
	val pairsState by viewModel.pairsState.collectAsState()
	val eventsState by viewModel.eventsState.collectAsState()
	val attendanceState by viewModel.attendanceState.collectAsState()
	val applicationState by viewModel.applicationState.collectAsState()
	var currentViewType by remember { mutableStateOf(StudentViewType.PAIRS) }
	var isRefreshing by remember { mutableStateOf(false) }

	val swipeRefreshState = rememberSwipeRefreshState(
		isRefreshing = isRefreshing
	)

	LaunchedEffect(Unit) {
		viewModel.getPairs()
		viewModel.getEvents()
	}

	LaunchedEffect(attendanceState) {
		if (attendanceState is AttendanceState.Success) {
			viewModel.resetAttendanceState()
		}
	}

	LaunchedEffect(applicationState) {
		if (applicationState is ApplicationState.Success) {
			viewModel.resetApplicationState()
		}
	}

	Scaffold(
		topBar = {
			Column {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 6.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = stringResource(id = R.string.my_pairs),
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.Bold,
						modifier = Modifier.weight(1f)
					)
					IconButton(onClick = onProfileClick) {
						Icon(
							imageVector = Icons.Default.Person,
							contentDescription = stringResource(id = R.string.profile),
							tint = colorResource(id = R.color.light_black),
							modifier = Modifier.size(28.dp)
						)
					}
				}
				
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 24.dp, vertical = 6.dp),
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					Button(
						onClick = { currentViewType = StudentViewType.PAIRS },
						modifier = Modifier.weight(1f),
						colors = androidx.compose.material3.ButtonDefaults.buttonColors(
							containerColor = if (currentViewType == StudentViewType.PAIRS) 
								colorResource(R.color.red) else colorResource(R.color.light_gray)
						)
					) {
						Text(
							text = stringResource(id = R.string.other_pairs),
							color = if (currentViewType == StudentViewType.PAIRS) 
								androidx.compose.ui.graphics.Color.White else colorResource(R.color.light_black),
							fontSize = 16.sp,
							fontWeight = FontWeight.Medium
						)
					}
					
					Button(
						onClick = { currentViewType = StudentViewType.EVENTS },
						modifier = Modifier.weight(1f),
						colors = androidx.compose.material3.ButtonDefaults.buttonColors(
							containerColor = if (currentViewType == StudentViewType.EVENTS) 
								colorResource(R.color.red) else colorResource(R.color.light_gray)
						)
					) {
						Text(
							text = stringResource(id = R.string.my_events_button),
							color = if (currentViewType == StudentViewType.EVENTS) 
								androidx.compose.ui.graphics.Color.White else colorResource(R.color.light_black),
							fontSize = 16.sp,
							fontWeight = FontWeight.Medium
						)
					}
				}
			}
		}
	) { padding ->
		SwipeRefresh(
			state = swipeRefreshState,
			onRefresh = {
				isRefreshing = true
				when (currentViewType) {
					StudentViewType.PAIRS -> viewModel.getPairs()
					StudentViewType.EVENTS -> viewModel.getEvents()
				}
			}
		) {
			LaunchedEffect(isRefreshing) {
				if (isRefreshing) {
					delay(1000)
					isRefreshing = false
				}
			}
			
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(padding)
			) {
				when (currentViewType) {
					StudentViewType.PAIRS -> {
						when (val state = pairsState) {
							is StudentPairsState.Loading -> {
								Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
									Text(stringResource(id = R.string.loading))
								}
							}

							is StudentPairsState.Error -> {
								Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
									Text(
										stringResource(id = R.string.error_with_colon, state.error),
										color = MaterialTheme.colorScheme.error
									)
								}
							}

							is StudentPairsState.Success -> {
								if (state.pairs.isEmpty()) {
									Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
										Text(stringResource(id = R.string.no_pairs_available))
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
											StudentLessonCard(
												lesson = pair.toLessonModel(),
												onAttendanceClick = {
													when (pair.status) {
														"DidNotVisit" -> {
															viewModel.markAttendance(pair.id)
														}
														"Pending" -> {
															viewModel.cancelAttendance(pair.id)
														}
														else -> {
														}
													}
												}
											)
										}
									}
								}
							}
						}
					}
					
					StudentViewType.EVENTS -> {
						when (val state = eventsState) {
							is StudentEventsState.Loading -> {
								Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
									Text(stringResource(id = R.string.loading))
								}
							}

							is StudentEventsState.Error -> {
								Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
									Text(
										stringResource(id = R.string.error_with_colon, state.error),
										color = MaterialTheme.colorScheme.error
									)
								}
							}

							is StudentEventsState.Success -> {
								if (state.events.isEmpty()) {
									Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
										Text(stringResource(id = R.string.no_my_events))
									}
								} else {
									LazyColumn(
										modifier = Modifier
											.fillMaxSize()
											.padding(top = 8.dp, bottom = 16.dp),
										verticalArrangement = Arrangement.spacedBy(16.dp),
										contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
									) {
										items(state.events) { event ->
											StudentEventCard(
												eventWithStatus = event,
												onApplicationClick = {
													when (event.status) {
														"DidNotVisit" -> {
															viewModel.createApplication(event.event.id)
														}
														"Pending" -> {
															viewModel.deleteApplication(event.event.id)
														}
														"Accepted" -> {
														}
														"Declined" -> {
														}
														null -> {
															viewModel.createApplication(event.event.id)
														}
														else -> {
														}
													}
												},
												onEventClick = { onEventClick(event.event.id) }
											)
										}
									}
								}
							}
						}
					}
				}

				when (attendanceState) {
					is AttendanceState.Loading -> {
						Box(
							Modifier.fillMaxSize(),
							contentAlignment = Alignment.Center
						) {
							Text(stringResource(id = R.string.updating))
						}
					}
					is AttendanceState.Error -> {
						Box(
							Modifier.fillMaxSize(),
							contentAlignment = Alignment.Center
						) {
							Text(
								text = stringResource(id = R.string.error_with_colon, (attendanceState as AttendanceState.Error).error),
								color = MaterialTheme.colorScheme.error
							)
						}
					}
					else -> {}
				}

				when (applicationState) {
					is ApplicationState.Loading -> {
						Box(
							Modifier.fillMaxSize(),
							contentAlignment = Alignment.Center
						) {
							Text(stringResource(id = R.string.updating))
						}
					}
					is ApplicationState.Error -> {
						Box(
							Modifier.fillMaxSize(),
							contentAlignment = Alignment.Center
						) {
							Text(
								text = stringResource(id = R.string.error_with_colon, (applicationState as ApplicationState.Error).error),
								color = MaterialTheme.colorScheme.error
							)
						}
					}
					else -> {}
				}
			}
		}
	}
} 