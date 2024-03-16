package com.baris.voltagedropapp.feature_voltage_drop.presentation.utils.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    title: String,
    icon: ImageVector = Icons.Filled.ArrowBack,
    showIcon: Boolean,
    onClick: () -> Unit = {}
) {
    TopAppBar(
        navigationIcon = {
            Row(modifier = Modifier.padding(start = 16.dp)) {
                if (showIcon) {
                    Icon(
                        imageVector = icon,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "icon",
                        modifier = Modifier.clickable {
                            onClick.invoke()
                        })
                } else Box {}
            }
        },
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 45.dp), horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        })
}