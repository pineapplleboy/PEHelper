package com.example.pehelper.presentation.component

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pehelper.R
import com.example.pehelper.data.model.AttendanceStudent
import com.example.pehelper.presentation.screen.AvatarViewModel
import com.example.pehelper.presentation.viewmodel.LessonStudentsViewModel

@Composable
fun StudentCard(
	student: AttendanceStudent,
	status: String,
	onAccept: (Int) -> Unit,
	onDecline: () -> Unit,
	viewModel: LessonStudentsViewModel
) {
	val context = LocalContext.current
	val (expanded, setExpanded) = remember { mutableStateOf(false) }
	val (selectedAmount, setSelectedAmount) = remember { mutableStateOf(1) }

	val avatarUri by produceState<Uri?>(null, student.avatarId) {
		value = student.avatarId?.let { viewModel.loadAvatar(context, it) }
	}

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
				if (avatarUri != null) {
					AsyncImage(
						model = avatarUri,
						contentDescription = "Фото ученика",
						modifier = Modifier.size(48.dp),
						contentScale = ContentScale.Crop
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
				Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
					student.name?.let {
						Text(
							text = it,
							fontWeight = FontWeight.Bold,
							fontSize = 16.sp,
							color = Color.Black
						)
					}
					if (status == "Declined") {
						Spacer(modifier = Modifier.size(8.dp))
						Text(
							text = "Отклонено",
							fontSize = 12.sp
						)
					} else if (status == "Accepted") {
						Spacer(modifier = Modifier.size(8.dp))
						Text(
							text = "Принят",
							fontSize = 12.sp
						)
					} else if (status == "Credited") {
						Spacer(modifier = Modifier.size(8.dp))
						Text(
							text = "Подтверждено",
							fontSize = 12.sp
						)
					} else if (status == "Pending"){
						Box(
							modifier = Modifier
								.border(
									shape = RoundedCornerShape(8.dp),
									border = BorderStroke(1.dp, Color.Black)
								)
								.clickable { setExpanded(true) }
						) {
							Text(
								text = selectedAmount.toString(),
								fontSize = 12.sp,
								modifier = Modifier.padding(start = 8.dp, end = 8.dp)
							)
							DropdownMenu(
								expanded = expanded,
								onDismissRequest = { setExpanded(false) }
							) {
								(1..3).forEach { amount ->
									DropdownMenuItem(
										text = { Text(amount.toString()) },
										onClick = {
											setSelectedAmount(amount)
											setExpanded(false)
										}
									)
								}
							}
						}
					}
				}

				Spacer(modifier = Modifier.size(8.dp))
				if (status == "Pending") {
					Row(
						horizontalArrangement = Arrangement.spacedBy(8.dp)
					) {
						OutlinedButton(
							onClick = onDecline,
							modifier = Modifier.weight(1f)
						) {
							Text(
								"Отклонить", style = MaterialTheme.typography.titleLarge,
								fontWeight = FontWeight.Bold,
								fontSize = 12.sp,
								modifier = Modifier
							)
						}
						Button(
							onClick = { onAccept(selectedAmount) },
							modifier = Modifier.weight(1f)
						) {
							Text(
								"Принять", style = MaterialTheme.typography.titleLarge,
								fontWeight = FontWeight.Bold,
								fontSize = 12.sp,
								modifier = Modifier
							)
						}
					}
				} else if (status == "Declined") {
					Row(
						horizontalArrangement = Arrangement.spacedBy(8.dp)
					) {
						Button(
							onClick = { onAccept(selectedAmount) },
							modifier = Modifier.weight(1f)
						) {
							Text(
								"Принять", style = MaterialTheme.typography.titleLarge,
								fontWeight = FontWeight.Bold,
								fontSize = 12.sp,
								modifier = Modifier
							)
						}
					}
				} else if (status == "Accepted"){
					Row(
						horizontalArrangement = Arrangement.spacedBy(8.dp)
					) {
						OutlinedButton(
							onClick = onDecline,
							modifier = Modifier.weight(1f)
						) {
							Text(
								"Отклонить", style = MaterialTheme.typography.titleLarge,
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
}