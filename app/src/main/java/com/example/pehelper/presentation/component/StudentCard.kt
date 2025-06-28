package com.example.pehelper.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pehelper.R

@Composable
fun StudentCard(
	fullName: String,
	photoRes: Int? = null,
	onAccept: () -> Unit,
	onDecline: () -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 6.dp),
		shape = RoundedCornerShape(18.dp),
		colors = CardDefaults.cardColors(containerColor = Color.White),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Box(
				modifier = Modifier
					.size(56.dp)
					.clip(CircleShape)
					.background(Color(0xFFE0E0E0)),
				contentAlignment = Alignment.Center
			) {
				if (photoRes != null) {
					Image(
						painter = painterResource(id = photoRes),
						contentDescription = "Фото ученика",
						modifier = Modifier.size(48.dp)
					)
				} else {
					Image(
						painter = painterResource(id = R.drawable.account_circle),
						contentDescription = "Фото ученика",
						modifier = Modifier.size(48.dp)
					)
				}
			}
			Spacer(modifier = Modifier.size(16.dp))
			Column(
				modifier = Modifier
			) {
				Text(
					text = fullName,
					fontWeight = FontWeight.Bold,
					fontSize = 16.sp,
					color = Color.Black
				)
				Spacer(modifier = Modifier.size(8.dp))
				Row(
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					OutlinedButton(onClick = onDecline,
						modifier = Modifier.weight(1f)) {
						Text("Отклонить",style = MaterialTheme.typography.titleLarge,
							fontWeight = FontWeight.Bold,
							fontSize = 12.sp,
							modifier = Modifier
						)
					}
					Button(onClick = onAccept,
						modifier = Modifier.weight(1f)) {
						Text("Принять",style = MaterialTheme.typography.titleLarge,
							fontWeight = FontWeight.Bold,
							fontSize = 12.sp,
							modifier = Modifier
						)
					}
				}
			}

		}
	}
}