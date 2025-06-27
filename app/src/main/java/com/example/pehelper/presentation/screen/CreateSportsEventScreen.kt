package com.example.pehelper.presentation.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pehelper.R
import com.example.pehelper.data.model.CreateSportsEventRequest
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateSportsEventScreen(
    onCreateClick: () -> Unit,
    onBack: () -> Unit,
    viewModel: SportsEventsViewModel = koinViewModel()
) {
    val createState by viewModel.createState.collectAsState()
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var classesAmount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateTime by remember { mutableStateOf(LocalDateTime.now()) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val isValid = name.isNotBlank() && classesAmount.isNotBlank() && description.isNotBlank()

    LaunchedEffect(createState) {
        if (createState is CreateEventState.Success) {
            onCreateClick()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Новое мероприятие",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                },
                windowInsets = WindowInsets(0),
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Отмена", color = colorResource(R.color.red), fontSize = 16.sp)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (isValid) {
                                viewModel.createEvent(
                                    CreateSportsEventRequest(
                                        name = name,
                                        classesAmount = classesAmount.toIntOrNull() ?: 0,
                                        description = description,
                                        date = dateTime
                                            .atZone(ZoneId.systemDefault())
                                            .withZoneSameInstant(ZoneOffset.UTC)
                                            .toLocalDateTime()
                                            .toString() + "Z"
                                    )
                                )
                            }
                        },
                        enabled = isValid && createState !is CreateEventState.Loading
                    ) {
                        Text(
                            if (createState is CreateEventState.Loading) "Создание..." else "Готово",
                            color = colorResource(R.color.red),
                            fontSize = 16.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Название", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            FieldBlock(
                label = "Количество занятий",
                value = classesAmount,
                onValueChange = { if (it.all { ch -> ch.isDigit() }) classesAmount = it },
                placeholder = "Например, 1"
            )

            FieldBlock(
                label = "Описание",
                value = description,
                onValueChange = { description = it },
                placeholder = "Кратко о мероприятии"
            )

            FieldPickerBlock(
                label = "Дата",
                value = dateTime.format(dateFormatter),
                onClick = {
                    val now = dateTime
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            dateTime = dateTime.withYear(year).withMonth(month + 1)
                                .withDayOfMonth(dayOfMonth)
                        },
                        now.year, now.monthValue - 1, now.dayOfMonth
                    ).show()
                }
            )
            
            FieldPickerBlock(
                label = "Время",
                value = dateTime.format(timeFormatter),
                onClick = {
                    val now = dateTime
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            dateTime = dateTime.withHour(hour).withMinute(minute)
                        },
                        now.hour, now.minute, true
                    ).show()
                }
            )

            if (createState is CreateEventState.Error) {
                Text(
                    (createState as CreateEventState.Error).error,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldBlock(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun FieldPickerBlock(label: String, value: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF5F5F5))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
