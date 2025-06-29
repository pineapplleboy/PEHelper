package com.example.pehelper.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pehelper.R
import com.example.pehelper.data.model.EventAttendance
import com.example.pehelper.data.model.PairAttendance
import com.example.pehelper.data.model.OtherActivity
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AllAttendancesScreen(
    navController: NavController,
    viewModel: AllAttendancesViewModel = koinViewModel()
) {
    val state by viewModel.attendancesState.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }

    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = isRefreshing
    )

    LaunchedEffect(Unit) {
        viewModel.getAllAttendances()
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow_ic),
                        contentDescription = stringResource(id = R.string.back),
                        tint = colorResource(id = R.color.black),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(id = R.string.all_attendances_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    ) { padding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                isRefreshing = true
                viewModel.getAllAttendances()
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
                when (val currentState = state) {
                    is AllAttendancesState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is AllAttendancesState.Error -> {
                        Text(
                            text = stringResource(
                                id = R.string.error_with_colon,
                                currentState.error
                            ),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is AllAttendancesState.Success -> {
                        AllAttendancesContent(data = currentState.data)
                    }

                    is AllAttendancesState.Idle -> { }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AllAttendancesContent(data: com.example.pehelper.data.model.AllAttendancesResponse) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (data.pairs.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(id = R.string.pairs_section),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            items(data.pairs) { pairAttendance ->
                PairAttendanceCard(pairAttendance = pairAttendance)
            }
        }

        if (data.otherActivities.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(id = R.string.other_activities_section),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            items(data.otherActivities) { otherActivity ->
                OtherActivityCard(otherActivity = otherActivity)
            }
        }

        if (data.events.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(id = R.string.events_section),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            items(data.events) { eventAttendance ->
                EventAttendanceCard(eventAttendance = eventAttendance)
            }
        }

        if (data.pairs.isEmpty() && data.events.isEmpty() && data.otherActivities.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.no_attendances),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorResource(id = R.color.gray)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PairAttendanceCard(pairAttendance: PairAttendance) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.light_gray)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        id = R.string.pair_number,
                        pairAttendance.pair.pairNumber
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = pairAttendance.pair.subject.name ?: "Предмет не указан",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Преподаватель: ${pairAttendance.pair.teacher.fullName}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.gray)
            )

            Text(
                text = "Дата: ${formatDate(pairAttendance.pair.date)}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.gray)
            )

            Text(
                text = "Количество занятий: ${pairAttendance.classesAmount}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.gray)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OtherActivityCard(otherActivity: OtherActivity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.light_gray)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.other_activity),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (otherActivity.comment.isNotEmpty()) {
                Text(
                    text = otherActivity.comment,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = "Дата: ${formatDate(otherActivity.date)}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.gray)
            )

            Text(
                text = "Количество занятий: ${otherActivity.classesAmount}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.gray)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventAttendanceCard(eventAttendance: EventAttendance) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.light_gray)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.event_name, eventAttendance.event.name),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = eventAttendance.event.description,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Дата: ${formatDate(eventAttendance.event.date)}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.gray)
            )

            Text(
                text = "Факультет: ${eventAttendance.event.faculty.name ?: "Не указан"}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.gray)
            )

            Text(
                text = "Количество занятий: ${eventAttendance.event.classesAmount}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.gray)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(dateString: String): String {
    return try {
        val utcDateTime = LocalDateTime.parse(dateString.replace("Z", ""))
        val utcZonedDateTime = utcDateTime.atZone(java.time.ZoneOffset.UTC)
        val localZonedDateTime = utcZonedDateTime.withZoneSameInstant(java.time.ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        localZonedDateTime.format(formatter)
    } catch (e: Exception) {
        "Дата не указана"
    }
} 