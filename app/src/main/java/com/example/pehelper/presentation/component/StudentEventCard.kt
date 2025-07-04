package com.example.pehelper.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pehelper.R
import com.example.pehelper.presentation.screen.StudentEventWithStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudentEventCard(
    eventWithStatus: StudentEventWithStatus,
    modifier: Modifier = Modifier,
    onApplicationClick: () -> Unit,
    onEventClick: () -> Unit
) {
    val event = eventWithStatus.event
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onEventClick() }
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = event.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_clock),
                            contentDescription = stringResource(id = R.string.time),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formatDate(event.date),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF999999)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(id = R.string.classes_amount) + ": ${event.classesAmount}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF666666)
                    )

                    eventWithStatus.status?.let { status ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(id = R.string.status) + ": ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = getStatusText(status),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = getStatusColor(status)
                            )
                        }
                    }
                }

                val shouldShowButton = shouldShowButton(eventWithStatus.status)
                if (shouldShowButton) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onApplicationClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (eventWithStatus.status == "Pending") Color(0xFFE57373) else Color(
                                0xFF4CAF50
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = getButtonText(eventWithStatus.status),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
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

@Composable
private fun getStatusText(status: String): String {
    return when (status) {
        "Pending" -> stringResource(id = R.string.status_pending_text)
        "Accepted" -> stringResource(id = R.string.status_accepted_text)
        "Declined" -> stringResource(id = R.string.status_declined_text)
        "DidNotVisit" -> stringResource(id = R.string.status_did_not_visit_text)
        "Credited" -> "Подтверждено"
        else -> status
    }
}

@Composable
private fun getStatusColor(status: String): Color {
    return when (status) {
        "Pending" -> Color(0xFFFF9800)
        "Accepted" -> Color(0xFF4CAF50)
        "Declined" -> Color(0xFFF44336)
        "DidNotVisit" -> Color(0xFF9E9E9E)
        "Credited" -> Color(0xFF4CAF50)
        else -> Color(0xFF666666)
    }
}

@Composable
private fun getButtonText(status: String?): String {
    return when (status) {
        "DidNotVisit" -> stringResource(id = R.string.mark_attendance)
        "Pending" -> stringResource(id = R.string.cancel_attendance)
        null -> stringResource(id = R.string.mark_attendance)
        else -> stringResource(id = R.string.mark_attendance)
    }
}

@Composable
private fun shouldShowButton(status: String?): Boolean {
    return when (status) {
        "DidNotVisit", "Pending" -> true
        "Accepted", "Declined", "Credited" -> false
        null -> true
        else -> true
    }
} 