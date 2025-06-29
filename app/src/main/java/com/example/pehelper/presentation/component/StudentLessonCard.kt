package com.example.pehelper.presentation.component

import androidx.compose.foundation.Image
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pehelper.R
import com.example.pehelper.data.model.LessonModel

@Composable
fun StudentLessonCard(
	lesson: LessonModel,
	modifier: Modifier = Modifier,
	onAttendanceClick: () -> Unit
) {
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
				verticalAlignment = Alignment.CenterVertically
			) {
				Column(
					modifier = Modifier.weight(1f)
				) {
					Text(
						text = lesson.title,
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
							text = lesson.time,
							fontSize = 14.sp,
							fontWeight = FontWeight.Medium,
							color = Color(0xFF999999)
						)
					}

					Spacer(modifier = Modifier.height(4.dp))

					Row(verticalAlignment = Alignment.CenterVertically) {
						Image(
							painter = painterResource(id = R.drawable.ic_person),
							contentDescription = stringResource(id = R.string.teacher),
							modifier = Modifier.size(18.dp)
						)
						Spacer(modifier = Modifier.width(6.dp))
						Text(
							text = lesson.teacherName,
							fontSize = 14.sp,
							fontWeight = FontWeight.Medium,
							color = Color(0xFF212121)
						)
					}
				}

				val shouldShowButton = shouldShowAttendanceButton(lesson.status)
				if (shouldShowButton) {
					Spacer(modifier = Modifier.width(16.dp))
					Button(
						onClick = onAttendanceClick,
						colors = ButtonDefaults.buttonColors(
							containerColor = if (lesson.status?.lowercase() == "pending") Color(0xFFE57373) else Color(0xFF4CAF50)
						),
						shape = RoundedCornerShape(12.dp)
					) {
						Text(
							text = getButtonText(lesson.status),
							fontSize = 14.sp,
							fontWeight = FontWeight.Medium,
							color = Color.White
						)
					}
				}
			}

			lesson.status?.let { status ->
				Spacer(modifier = Modifier.height(12.dp))
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
	}
}

@Composable
private fun shouldShowAttendanceButton(status: String?): Boolean {
	return when (status?.lowercase()) {
		"didnotvisit", "pending" -> true
		"accepted", "declined", "credited" -> false
		else -> true
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
private fun getStatusText(status: String): String {
	return when (status.lowercase()) {
		"pending" -> stringResource(id = R.string.status_pending_text)
		"accepted" -> stringResource(id = R.string.status_accepted_text)
		"declined" -> stringResource(id = R.string.status_declined_text)
		"didnotvisit" -> stringResource(id = R.string.status_did_not_visit_text)
		"credited" -> "Подтверждено"
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
		"credited" -> Color(0xFF4CAF50)
		else -> Color(0xFF666666)
	}
}