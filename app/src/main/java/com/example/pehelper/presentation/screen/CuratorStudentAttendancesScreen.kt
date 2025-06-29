package com.example.pehelper.presentation.screen

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pehelper.R
import com.example.pehelper.data.model.CuratorStudentProfileResponse
import com.example.pehelper.data.network.ApiConstants
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
    val context = LocalContext.current

    LaunchedEffect(studentId) {
        viewModel.loadProfile(studentId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (val s = state) {
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
                        text = s.error,
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
                val data = s.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val avatarId = data.student.avatarId
                        if (avatarId != null) {
                            AsyncImage(
                                model = ApiConstants.getAvatarUrl(avatarId),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.account_circle),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape),
                                tint = colorResource(id = R.color.gray)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = data.student.fullName ?: "-",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Группа: ${data.student.group ?: ""}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Курс: ${data.student.course ?: "-"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Факультет: ${data.student.faculty?.name ?: "-"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (data.pairs.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Пары",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            items(data.pairs) { pair ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text("Пара №${pair.pair.pairNumber ?: "-"}", fontWeight = FontWeight.Bold)
                                        Text("Предмет: ${pair.pair.subject?.name ?: "Не указан"}")
                                        Text("Преподаватель: ${pair.pair.teacher?.fullName ?: "Не указан"}")
                                        if (!pair.pair.date.isNullOrBlank()) {
                                            Text("Дата: ${formatDate(pair.pair.date)}")
                                        }
                                        Text("Статус: ${getAttendanceStatusText(pair.status)}")
                                        Text("Часов: ${pair.classesAmount ?: 0}")
                                    }
                                }
                            }
                        }
                        if (data.events.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Мероприятия",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            items(data.events) { event ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text(event.event.name ?: "Название не указано", fontWeight = FontWeight.Bold)
                                        Text("Описание: ${event.event.description ?: "Описание не указано"}")
                                        if (!event.event.date.isNullOrBlank()) {
                                            Text("Дата: ${formatDate(event.event.date)}")
                                        }
                                        Text("Статус: ${getAttendanceStatusText(event.status)}")
                                        Text("Часов: ${event.event.classesAmount ?: 0}")
                                        if (event.event.faculty != null) {
                                            Text("Факультет: ${event.event.faculty.name ?: "Не указан"}")
                                        }
                                    }
                                }
                            }
                        }
                        if (data.otherActivities.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Другая активность",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            items(data.otherActivities) { activity ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text(activity.comment ?: "-")
                                        if (!activity.date.isNullOrBlank()) {
                                            Text("Дата: ${formatDate(activity.date)}")
                                        }
                                        Text("Часов: ${activity.classesAmount ?: 0}")
                                    }
                                }
                            }
                        }
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

private fun getAttendanceStatusText(status: String?): String {
    return when (status) {
        "Visited" -> "Посещено"
        "DidNotVisit" -> "Не посещено"
        "Pending" -> "Ожидает подтверждения"
        "Accepted" -> "Принято"
        "Rejected" -> "Отклонено"
        else -> status ?: "Неизвестно"
    }
}

@SuppressLint("NewApi")
private fun formatDate(date: String?): String {
    return try {
        if (date.isNullOrBlank()) {
            return "Дата не указана"
        }
        
        val cleanDate = date.replace("Z", "").trim()
        if (cleanDate.isEmpty()) {
            return "Дата не указана"
        }
        
        val localDateTime = try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
            LocalDateTime.parse(cleanDate, formatter)
        } catch (e: Exception) {
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
                LocalDateTime.parse(cleanDate, formatter)
            } catch (e2: Exception) {
                try {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                    LocalDateTime.parse(cleanDate, formatter)
                } catch (e3: Exception) {
                    try {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
                        LocalDateTime.parse(cleanDate, formatter)
                    } catch (e4: Exception) {
                        return "Неизвестный формат даты"
                    }
                }
            }
        }
        val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        localDateTime.format(outputFormatter)
    } catch (e: Exception) {
        "Ошибка формата даты"
    }
}