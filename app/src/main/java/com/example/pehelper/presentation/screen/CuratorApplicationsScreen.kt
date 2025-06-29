package com.example.pehelper.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import com.example.pehelper.presentation.component.SwipeableApplicationCard
import com.example.pehelper.R
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CuratorApplicationsScreen(
    onProfileClick: () -> Unit = {},
    viewModel: CuratorApplicationsViewModel = koinViewModel(),
    navController: NavController? = null
) {
    val applicationsState by viewModel.applicationsState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var currentApplicationIndex by remember { mutableStateOf(0) }

    val selectedTab = remember { mutableStateOf(0) }

    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = isRefreshing
    )

    LaunchedEffect(Unit) {
        viewModel.getApplications()
    }

    LaunchedEffect(actionState) {
        if (actionState is ApplicationActionState.Success) {
            viewModel.getApplications()
            currentApplicationIndex = 0
            viewModel.resetActionState()
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab.value = 0 },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Заявки",
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
                        text = "Занятия",
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
                IconButton(onClick = onProfileClick) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(id = R.string.profile),
                        tint = colorResource(id = R.color.light_black),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }


        }
    ) { padding ->
        if (selectedTab.value == 0) {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    isRefreshing = true
                    currentApplicationIndex = 0
                    viewModel.getApplications()
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
                    when (val state = applicationsState) {
                        is ApplicationsListState.Loading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(stringResource(id = R.string.loading))
                            }
                        }

                        is ApplicationsListState.Error   -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(stringResource(id = R.string.error_with_colon, state.error), color = MaterialTheme.colorScheme.error)
                            }
                        }

                        is ApplicationsListState.Success -> {
                            if (state.applications.isEmpty()) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(stringResource(id = R.string.no_applications))
                                }
                            } else if (currentApplicationIndex >= state.applications.size) {

                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = stringResource(id = R.string.all_applications_processed),
                                        style = MaterialTheme.typography.titleMedium,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            } else {
                                val currentApplication = state.applications[currentApplicationIndex]
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SwipeableApplicationCard(
                                        application = currentApplication,
                                        onApprove = {
                                            viewModel.approveApplication(
                                                currentApplication.event.id,
                                                currentApplication.profile.id ?: ""
                                            )
                                        },
                                        onReject = {
                                            viewModel.rejectApplication(
                                                currentApplication.event.id,
                                                currentApplication.profile.id ?: ""
                                            )
                                        },
                                        isActionLoading = actionState is ApplicationActionState.Loading,
                                        onProfileClick = { studentId ->
                                            navController?.navigate("curator_student_profile/$studentId")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            CuratorLessonsContent(
                navController = navController
            )
        }
    }
} 