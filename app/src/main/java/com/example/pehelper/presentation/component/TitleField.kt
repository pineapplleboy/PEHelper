package com.example.pehelper.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pehelper.R

@Composable
fun TitleField(
    loginText: String,
    accessText: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(horizontal = 24.dp)) {
        Image(
            painter = painterResource(R.drawable.tsu_icon),
            contentDescription = null,
            modifier = Modifier
                .width(48.dp)
                .height(48.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = loginText,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = accessText,
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.light_black)
            )
        }
    }
}

