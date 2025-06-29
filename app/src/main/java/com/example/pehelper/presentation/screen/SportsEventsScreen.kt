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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pehelper.presentation.component.SportsEventCard
import com.example.pehelper.presentation.component.StudentLessonCard
import com.example.pehelper.data.model.toLessonModel
import com.example.pehelper.R
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SportsEventsScreen(
    onCreateEventClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    viewModel: SportsEventsViewModel = koinViewModel(),
    navController: NavController? = null
) {
    val eventsState by viewModel.eventsState.collectAsState()
    val pairsState by viewModel.pairsState.collectAsState()
    val attendanceState by viewModel.attendanceState.collectAsState()
    val currentViewType by viewModel.currentViewType.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }

    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = isRefreshing
    )

    LaunchedEffect(Unit) {
        viewModel.getEvents()
        viewModel.getPairs()
    }

    LaunchedEffect(attendanceState) {
        if (attendanceState is AttendanceState.Success) {
            viewModel.resetAttendanceState()
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
                        onClick = { viewModel.switchToOtherPairs() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentViewType == SportsOrganizerViewType.OTHER_PAIRS) 
                                colorResource(R.color.red) else colorResource(R.color.light_gray)
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.other_pairs),
                            color = if (currentViewType == SportsOrganizerViewType.OTHER_PAIRS) 
                                Color.White else colorResource(R.color.light_black),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Button(
                        onClick = { viewModel.switchToMyEvents() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentViewType == SportsOrganizerViewType.MY_EVENTS) 
                                colorResource(R.color.red) else colorResource(R.color.light_gray)
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.my_events_button),
                            color = if (currentViewType == SportsOrganizerViewType.MY_EVENTS) 
                                Color.White else colorResource(R.color.light_black),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentViewType == SportsOrganizerViewType.MY_EVENTS) {
                FloatingActionButton(onClick = {
                    onCreateEventClick()
                    viewModel.getEvents()
                }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.create_event))
                }
            }
        }
    ) { padding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                isRefreshing = true
                when (currentViewType) {
                    SportsOrganizerViewType.OTHER_PAIRS -> viewModel.getPairs()
                    SportsOrganizerViewType.MY_EVENTS -> viewModel.getEvents()
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
                    SportsOrganizerViewType.OTHER_PAIRS -> {
                        when (val state = pairsState) {
                            is PairsListState.Loading -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(stringResource(id = R.string.loading))
                                }
                            }
                            is PairsListState.Error -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(stringResource(id = R.string.error_with_colon, state.error), color = MaterialTheme.colorScheme.error)
                                }
                            }
                            is PairsListState.Success -> {
                                if (state.pairs.isEmpty()) {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text(stringResource(id = R.string.no_other_pairs))
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
                                                        "DidNotVisit" -> viewModel.markAttendance(pair.id)
                                                        "Pending" -> viewModel.cancelAttendance(pair.id)
                                                        else -> {
                                                            if (pair.isAttended) {
                                                                viewModel.cancelAttendance(pair.id)
                                                            } else {
                                                                viewModel.markAttendance(pair.id)
                                                            }
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
                    SportsOrganizerViewType.MY_EVENTS -> {
                        when (val state = eventsState) {
                            is EventsListState.Loading -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(stringResource(id = R.string.loading))
                                }
                            }
                            is EventsListState.Error -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(stringResource(id = R.string.error_with_colon, state.error), color = MaterialTheme.colorScheme.error)
                                }
                            }
                            is EventsListState.Success -> {
                                if (state.events.isEmpty()) {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text(stringResource(id = R.string.no_events))
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
                                            SportsEventCard(event = event, onClick = {
                                                navController?.navigate("sports_event_detail/${event.id}")
                                            })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}