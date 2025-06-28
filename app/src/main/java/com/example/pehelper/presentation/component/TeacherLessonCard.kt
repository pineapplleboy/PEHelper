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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pehelper.R
import com.example.pehelper.data.model.LessonModel

@Composable
fun TeacherLessonCard(
	lesson: LessonModel,
	modifier: Modifier = Modifier
) {
	Card(
		modifier = modifier
			.fillMaxWidth(),
		shape = RoundedCornerShape(20.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White),
		elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 24.dp, vertical = 16.dp),
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
						contentDescription = "Время",
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
						contentDescription = "Преподаватель",
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
		}
	}
}

@Preview
@Composable
fun TeacherLessonPreview() {
	LessonCard(
		lesson = LessonModel(
			title = "Бодибилдинг",
			time = "16:30 - 18:00",
			teacherName = "Пронькин Александр Александрович",
			isVisited = true
		)
	)
}