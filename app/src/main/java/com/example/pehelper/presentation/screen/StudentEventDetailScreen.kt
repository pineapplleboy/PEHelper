package com.example.pehelper.presentation.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pehelper.R
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudentEventDetailScreen(
	eventId: String,
	onBackClick: () -> Unit = {},
	viewModel: StudentEventDetailViewModel = koinViewModel(),
	navController: NavController? = null
) {
	val eventState by viewModel.eventState.collectAsState()
	val applicationState by viewModel.applicationState.collectAsState()
	var isRefreshing by remember { mutableStateOf(false) }

	val swipeRefreshState = rememberSwipeRefreshState(
		isRefreshing = isRefreshing
	)

	LaunchedEffect(eventId) {
		viewModel.getEventDetail(eventId)
	}

	LaunchedEffect(applicationState) {
		if (applicationState is ApplicationState.Success) {
			viewModel.resetApplicationState()
		}
	}

	Scaffold(
		topBar = {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				IconButton(onClick = onBackClick) {
					Icon(
						imageVector = Icons.Default.ArrowBack,
						contentDescription = stringResource(id = R.string.back),
						tint = Color.Black,
						modifier = Modifier.size(28.dp)
					)
				}
				Spacer(modifier = Modifier.width(16.dp))
				Text(
					text = stringResource(id = R.string.event_details),
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.weight(1f)
				)
			}
		}
	) { padding ->
		SwipeRefresh(
			state = swipeRefreshState,
			onRefresh = {
				isRefreshing = true
				viewModel.getEventDetail(eventId)
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
				when (val state = eventState) {
					is StudentEventDetailState.Loading -> {
						Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
							Text(stringResource(id = R.string.loading))
						}
					}

					is StudentEventDetailState.Error -> {
						Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
							Text(
								stringResource(id = R.string.error_with_colon, state.error),
								color = MaterialTheme.colorScheme.error
							)
						}
					}

					is StudentEventDetailState.Success -> {
						val event = state.event
						Column(
							modifier = Modifier
								.fillMaxSize()
								.padding(horizontal = 16.dp)
								.verticalScroll(rememberScrollState())
						) {
							Card(
								modifier = Modifier.fillMaxWidth(),
								shape = RoundedCornerShape(20.dp),
								colors = CardDefaults.cardColors(containerColor = Color.White),
								elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
							) {
								Column(
									modifier = Modifier
										.fillMaxWidth()
										.padding(24.dp)
								) {
									Text(
										text = event.name,
										fontSize = 24.sp,
										fontWeight = FontWeight.ExtraBold,
										color = Color.Black
									)

									Spacer(modifier = Modifier.height(16.dp))

									Row(verticalAlignment = Alignment.CenterVertically) {
										Image(
											painter = painterResource(id = R.drawable.ic_clock),
											contentDescription = stringResource(id = R.string.time),
											modifier = Modifier.size(20.dp)
										)
										Spacer(modifier = Modifier.width(8.dp))
										Text(
											text = formatDate(event.date),
											fontSize = 16.sp,
											fontWeight = FontWeight.Medium,
											color = Color(0xFF666666)
										)
									}

									Spacer(modifier = Modifier.height(12.dp))

									Row(verticalAlignment = Alignment.CenterVertically) {
										Image(
											painter = painterResource(id = R.drawable.ic_person),
											contentDescription = stringResource(id = R.string.faculty),
											modifier = Modifier.size(20.dp)
										)
										Spacer(modifier = Modifier.width(8.dp))
										Text(
											text = event.faculty.name ?: "Факультет не указан",
											fontSize = 16.sp,
											fontWeight = FontWeight.Medium,
											color = Color(0xFF666666)
										)
									}

									Spacer(modifier = Modifier.height(12.dp))

									Row(verticalAlignment = Alignment.CenterVertically) {
										Image(
											painter = painterResource(id = R.drawable.calendar_pass),
											contentDescription = stringResource(id = R.string.classes_amount),
											modifier = Modifier.size(20.dp)
										)
										Spacer(modifier = Modifier.width(8.dp))
										Text(
											text = stringResource(id = R.string.classes_amount) + ": ${event.classesAmount}",
											fontSize = 16.sp,
											fontWeight = FontWeight.Medium,
											color = Color(0xFF666666)
										)
									}

									Spacer(modifier = Modifier.height(16.dp))

									Text(
										text = stringResource(id = R.string.description),
										fontSize = 18.sp,
										fontWeight = FontWeight.Bold,
										color = Color.Black
									)

									Spacer(modifier = Modifier.height(8.dp))

									Text(
										text = event.description,
										fontSize = 16.sp,
										fontWeight = FontWeight.Medium,
										color = Color(0xFF666666)
									)

									Spacer(modifier = Modifier.height(16.dp))

									event.status?.let { status ->
										Row(verticalAlignment = Alignment.CenterVertically) {
											Text(
												text = stringResource(id = R.string.status) + ": ",
												fontSize = 16.sp,
												fontWeight = FontWeight.Medium,
												color = Color(0xFF666666)
											)
											Text(
												text = getStatusText(status),
												fontSize = 16.sp,
												fontWeight = FontWeight.Medium,
												color = getStatusColor(status)
											)
										}

										Spacer(modifier = Modifier.height(16.dp))
									}

									val shouldShowButton = shouldShowButton(event.status)
									if (shouldShowButton) {
										Button(
											onClick = {
												when (event.status) {
													"DidNotVisit" -> {
														viewModel.createApplication(event.id)
													}
													"Pending" -> {
														viewModel.deleteApplication(event.id)
													}
													else -> {
														// Никаких действий для других статусов
													}
												}
											},
											modifier = Modifier.fillMaxWidth(),
											colors = ButtonDefaults.buttonColors(
												containerColor = if (event.status?.lowercase() == "pending") Color(0xFFE57373) else Color(0xFF4CAF50)
											),
											shape = RoundedCornerShape(12.dp)
										) {
											Text(
												text = getButtonText(event.status),
												fontSize = 16.sp,
												fontWeight = FontWeight.Medium,
												color = Color.White
											)
										}
									}
								}
							}

							Spacer(modifier = Modifier.height(16.dp))
						}
					}
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

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(dateString: String): String {
	return try {
		val dateTime = LocalDateTime.parse(dateString.replace("Z", ""))
		val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
		dateTime.format(formatter)
	} catch (e: Exception) {
		"Дата не указана"
	}
}

@Composable
private fun getStatusText(status: String): String {
	return when (status.lowercase()) {
		"pending" -> stringResource(id = R.string.status_pending_text)
		"accepted" -> stringResource(id = R.string.status_accepted_text)
		"declined" -> stringResource(id = R.string.status_declined_text)
		"didnotvisit" -> stringResource(id = R.string.status_did_not_visit_text)
		else -> status
	}
}

@Composable
private fun getStatusColor(status: String): Color {
	return when (status.lowercase()) {
		"pending" -> Color(0xFFFF9800)
		"accepted" -> Color(0xFF4CAF50)
		"declined" -> Color(0xFFF44336)
		"didnotvisit" -> Color(0xFF9E9E9E)
		else -> Color(0xFF666666)
	}
}

@Composable
private fun getButtonText(status: String?): String {
	return when (status?.lowercase()) {
		"didnotvisit" -> stringResource(id = R.string.mark_attendance)
		"pending" -> stringResource(id = R.string.cancel_attendance)
		else -> stringResource(id = R.string.mark_attendance)
	}
}

@Composable
private fun shouldShowButton(status: String?): Boolean {
	return when (status?.lowercase()) {
		"didnotvisit", "pending" -> true
		"accepted", "declined" -> false
		else -> true
	}
} 