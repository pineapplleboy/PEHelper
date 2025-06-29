package com.example.pehelper.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pehelper.R
import com.example.pehelper.data.model.CuratorGroupStudent
import com.example.pehelper.data.network.ApiConstants
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.Image

@Composable
fun CuratorGroupDetailScreen(
    groupNumber: String,
    onBack: () -> Unit,
    onStudentClick: (String) -> Unit,
    viewModel: CuratorGroupsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val createActivityState by viewModel.createActivityState.collectAsState()
    var showActivityDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<CuratorGroupStudent?>(null) }

    LaunchedEffect(groupNumber) {
        viewModel.loadGroup(groupNumber)
    }

    LaunchedEffect(createActivityState) {
        if (createActivityState is CreateActivityState.Success) {
            showActivityDialog = false
            selectedStudent = null
            viewModel.resetCreateActivityState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (val currentState = state) {
            is CuratorGroupsState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is CuratorGroupsState.Error -> {
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
            is CuratorGroupsState.Success -> {
                val data = currentState.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .padding(top = 60.dp)
                ) {
                    // Заголовок
                    Text(
                        text = "Группа ${data.group ?: ""}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.black)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Факультет: ${data.faculty?.name ?: "Не указан"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorResource(id = R.color.gray)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Студенты (${data.students?.size ?: 0}):",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.black)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (data.students.isNullOrEmpty()) {
                        Text(
                            text = "Студенты не найдены",
                            color = colorResource(id = R.color.gray),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(data.students) { student ->
                                StudentCard(
                                    student = student,
                                    onClick = { student.id?.let { onStudentClick(it) } },
                                    onGiveClasses = {
                                        selectedStudent = student
                                        showActivityDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
            else -> {}
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

    if (showActivityDialog && selectedStudent != null) {
        CreateActivityDialog(
            student = selectedStudent!!,
            onDismiss = {
                showActivityDialog = false
                selectedStudent = null
                viewModel.resetCreateActivityState()
            },
            onCreateActivity = { comment, classesAmount ->
                selectedStudent?.id?.let { studentId ->
                    viewModel.createStudentActivity(studentId, comment, classesAmount)
                }
            },
            isLoading = createActivityState is CreateActivityState.Loading,
            error = if (createActivityState is CreateActivityState.Error) {
                (createActivityState as CreateActivityState.Error).error
            } else null
        )
    }
}

@Composable
private fun StudentCard(
    student: CuratorGroupStudent,
    onClick: () -> Unit,
    onGiveClasses: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Аватар
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { onClick() }
                ) {
                    if (student.avatarId != null) {
                        AsyncImage(
                            model = ApiConstants.getAvatarUrl(student.avatarId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.account_circle),
                            placeholder = painterResource(id = R.drawable.account_circle),
                            onError = {
                                android.util.Log.e("Avatar", "Failed to load avatar for student: ${student.name}, avatarId: ${student.avatarId}")
                            },
                            onSuccess = {
                                android.util.Log.d("Avatar", "Successfully loaded avatar for student: ${student.name}, avatarId: ${student.avatarId}")
                            }
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.account_circle),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name ?: "Не указано",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.black)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Курс: ${student.course ?: "Не указан"}",
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.gray)
                    )
                }

                Button(
                    onClick = onGiveClasses,
                    modifier = Modifier.padding(start = 8.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.red)
                    )
                ) {
                    Text(
                        text = "Дать занятия",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateActivityDialog(
    student: CuratorGroupStudent,
    onDismiss: () -> Unit,
    onCreateActivity: (String, Int) -> Unit,
    isLoading: Boolean,
    error: String?
) {
    var comment by remember { mutableStateOf("") }
    var classesAmount by remember { mutableStateOf("1") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Дать занятия студенту",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.black),
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = student.name ?: "Не указано",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(id = R.color.gray),
                    fontSize = 12.sp
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                OutlinedTextField(
                    value = classesAmount,
                    onValueChange = { classesAmount = it },
                    label = { Text("Количество занятий", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Комментарий", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                )
                
                if (error != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isLoading
                    ) {
                        Text("Отмена", fontSize = 12.sp)
                    }
                    
                    Spacer(modifier = Modifier.width(6.dp))
                    
                    Button(
                        onClick = {
                            val amount = classesAmount.toIntOrNull() ?: 1
                            onCreateActivity(comment, amount)
                        },
                        enabled = !isLoading && classesAmount.isNotEmpty(),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.red)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("Создать", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
} 