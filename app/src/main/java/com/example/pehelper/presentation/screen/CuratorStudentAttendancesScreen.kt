package com.example.pehelper.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pehelper.R
import com.example.pehelper.data.model.CuratorStudentProfileResponse
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CuratorStudentAttendancesScreen(
    studentId: String,
    onBack: () -> Unit,
    viewModel: CuratorStudentProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(studentId) {
        viewModel.loadProfile(studentId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (val currentState = state) {
            is CuratorStudentProfileState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is CuratorStudentProfileState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentState.error,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Попробуйте вернуться назад и повторить",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorResource(id = R.color.gray)
                    )
                }
            }
            is CuratorStudentProfileState.Success -> {
                val data = currentState.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.all_attendances_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.black)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${stringResource(R.string.student)}: ${data.student.fullName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorResource(id = R.color.gray)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    if (data.pairs.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.pairs_section),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.black)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        data.pairs.forEach { pair ->
                            AttendanceCard(
                                title = stringResource(R.string.pair_number, pair.pair.pairNumber ?: 0),
                                subtitle = pair.pair.subject?.name ?: "",
                                teacher = pair.pair.teacher?.fullName ?: "",
                                date = pair.pair.date,
                                status = pair.status,
                                classesAmount = pair.classesAmount
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (data.events.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.events_section),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.black)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        data.events.forEach { event ->
                            AttendanceCard(
                                title = stringResource(R.string.event_name, event.event.name ?: ""),
                                subtitle = event.event.description ?: "",
                                teacher = "",
                                date = event.event.date,
                                status = event.status,
                                classesAmount = event.event.classesAmount
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (data.otherActivities.isNotEmpty()) {
                        Text(
                            text = "Другая активность",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.black)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        data.otherActivities.forEach { activity ->
                            AttendanceCard(
                                title = activity.comment ?: "",
                                subtitle = "",
                                teacher = "",
                                date = activity.date,
                                status = null,
                                classesAmount = activity.classesAmount
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    if (data.pairs.isEmpty() && data.events.isEmpty() && data.otherActivities.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_attendances),
                            color = colorResource(id = R.color.gray),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AttendanceCard(
    title: String,
    subtitle: String,
    teacher: String,
    date: String?,
    status: String?,
    classesAmount: Int?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = colorResource(id = R.color.light_gray),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = colorResource(id = R.color.black)
            )
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.gray)
                )
            }
            if (teacher.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Преподаватель: $teacher",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(id = R.color.gray)
                )
            }
            if (date != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Дата: ${formatDate(date)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(id = R.color.gray)
                )
            }
            if (classesAmount != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Занятий: $classesAmount",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(id = R.color.gray)
                )
            }
            if (status != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Статус: ${getStatusText(status)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = getStatusColor(status)
                )
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
        dateString
    }
}

@Composable
private fun getStatusText(status: String): String {
    return when (status) {
        "Accepted" -> stringResource(R.string.attendance_status_accepted)
        "Pending" -> stringResource(R.string.attendance_status_pending)
        "Declined" -> stringResource(R.string.attendance_status_declined)
        "DidNotVisit" -> stringResource(R.string.attendance_status_did_not_visit)
        else -> status
    }
}

@Composable
private fun getStatusColor(status: String): androidx.compose.ui.graphics.Color {
    return when (status) {
        "Accepted" -> colorResource(R.color.status_accepted)
        "Pending" -> colorResource(R.color.status_pending)
        "Declined" -> colorResource(R.color.status_declined)
        "DidNotVisit" -> colorResource(R.color.status_declined)
        else -> colorResource(R.color.gray)
    }
} 