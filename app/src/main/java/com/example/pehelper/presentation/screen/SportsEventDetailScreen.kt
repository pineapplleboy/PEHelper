package com.example.pehelper.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pehelper.R
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SportsEventDetailScreen(
    eventId: String,
    onBack: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: SportsEventsViewModel = koinViewModel()
) {
    val state by viewModel.eventDetailState.collectAsState()
    val showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(eventId) {
        viewModel.getEventById(eventId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
            .padding(24.dp)
    ) {
        when (state) {
            is EventDetailState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is EventDetailState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = (state as EventDetailState.Error).error,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack) {
                        Text(stringResource(id = R.string.back))
                    }
                }
            }
            is EventDetailState.Deleted -> {
                LaunchedEffect(Unit) { onDeleted() }
            }
            is EventDetailState.Success -> {
                val event = (state as EventDetailState.Success).event
                val dateTime = try {
                    val utcDateTime = LocalDateTime.parse(event.date.replace("Z", ""))
                    val utcZonedDateTime = utcDateTime.atZone(java.time.ZoneOffset.UTC)
                    utcZonedDateTime.withZoneSameInstant(java.time.ZoneId.systemDefault())
                } catch (e: Exception) {
                    null
                }
                val formattedDate = dateTime?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) ?: event.date
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                painter = painterResource(id = R.drawable.back_arrow_ic),
                                contentDescription = stringResource(id = R.string.back),
                                tint = colorResource(R.color.red)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = event.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colorResource(id = R.color.black),
                            modifier = Modifier.weight(8f),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { showDialog.value = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.delete_ic_pass),
                                contentDescription = stringResource(id = R.string.delete_event),
                                tint = colorResource(R.color.red)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoBlock(label = stringResource(id = R.string.date_and_time), value = formattedDate)
                    InfoBlock(label = stringResource(id = R.string.classes_amount), value = event.classesAmount.toString())
                    InfoBlock(label = stringResource(id = R.string.faculty), value = event.faculty?.name ?: "-")
                    InfoBlock(label = stringResource(id = R.string.description), value = event.description)
                    Spacer(modifier = Modifier.height(32.dp))

                    if (!event.pendingAttendances.isNullOrEmpty()) {
                        Text(
                            text = stringResource(id = R.string.pending_applications),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colorResource(id = R.color.black),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            event.pendingAttendances.forEach { attendance ->
                                StudentAttendanceCard(
                                    profile = attendance.profile,
                                    status = attendance.status,
                                    onAccept = { viewModel.updateAttendanceStatus(event.id, attendance.profile.id ?: return@StudentAttendanceCard, "Accepted") },
                                    onDecline = { viewModel.updateAttendanceStatus(event.id, attendance.profile.id ?: return@StudentAttendanceCard, "Declined") },
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    Text(
                        text = stringResource(id = R.string.students_attendance_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colorResource(id = R.color.black),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    if (event.attendances.isNullOrEmpty()) {
                        Text(stringResource(id = R.string.no_students_attendance), color = colorResource(id = R.color.gray))
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            event.attendances.forEach { attendance ->
                                StudentAttendanceCard(
                                    profile = attendance.profile,
                                    status = attendance.status,
                                    onAccept = { viewModel.updateAttendanceStatus(event.id, attendance.profile.id ?: return@StudentAttendanceCard, "Accepted") },
                                    onDecline = { viewModel.updateAttendanceStatus(event.id, attendance.profile.id ?: return@StudentAttendanceCard, "Declined") },
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
                if (showDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showDialog.value = false },
                        title = { Text(stringResource(id = R.string.delete_event)) },
                        text = { Text(stringResource(id = R.string.delete_event_message)) },
                        confirmButton = {
                            TextButton(onClick = {
                                showDialog.value = false
                                viewModel.deleteEvent(eventId)
                            }) {
                                Text(stringResource(id = R.string.delete_event_confirm), color = colorResource(R.color.red))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog.value = false }) {
                                Text(stringResource(id = R.string.delete_event_cancel))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoBlock(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colorResource(id = R.color.gray)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = colorResource(id = R.color.light_black),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StudentAttendanceCard(
    profile: com.example.pehelper.data.model.AttendanceProfile,
    status: String,
    onAccept: (() -> Unit)? = null,
    onDecline: (() -> Unit)? = null
) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = colorResource(id = R.color.white)),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.account_circle),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(profile.fullName ?: "-", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colorResource(id = R.color.black))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when (status) {
                        "Accepted" -> stringResource(id = R.string.status_accepted)
                        "Declined" -> stringResource(id = R.string.status_declined)
                        "Credited" -> stringResource(id = R.string.status_credited)
                        else -> stringResource(id = R.string.status_pending)
                    },
                    color = when (status) {
                        "Accepted" -> colorResource(id = R.color.status_accepted)
                        "Declined" -> colorResource(id = R.color.status_declined)
                        "Credited" -> colorResource(id = R.color.status_accepted)
                        else -> colorResource(id = R.color.status_pending)
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = { onAccept?.invoke() },
                        enabled = status == "Pending" || status == "Declined",
                        modifier = Modifier
                            .height(32.dp)
                            .defaultMinSize(minWidth = 1.dp)
                            .padding(end = 8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(id = R.string.accept), fontSize = 14.sp)
                    }
                    Button(
                        onClick = { onDecline?.invoke() },
                        enabled = status == "Pending" || status == "Accepted",
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.red)),
                        modifier = Modifier
                            .height(32.dp)
                            .defaultMinSize(minWidth = 1.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(id = R.string.decline), color = colorResource(id = R.color.white), fontSize = 14.sp)
                    }
                }
            }
        }
    }
} 