package com.example.kreedaankana.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kreedaankana.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    label: String,
    value: String,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour = 16,
        initialMinute = 0,
        is24Hour = false
    )

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = if (label.isNotEmpty()) { { Text(label, color = SportGreyLight, fontSize = 12.sp, fontWeight = FontWeight.Bold) } } else null,
        placeholder = { Text("Select Time", color = SportGreyMuted) },
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = "Pick Time",
                    tint = SportOrange
                )
            }
        },
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = SportWhite,
            unfocusedTextColor = SportWhite,
            focusedBorderColor = SportOrange,
            unfocusedBorderColor = SportBorderLight,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp)
    )

    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            containerColor = SportSurface2,
            title = {
                Text(
                    text = if (label.isNotEmpty()) "SELECT $label" else "SELECT TIME",
                    color = SportWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = SportSurface,
                            clockDialSelectedContentColor = SportWhite,
                            clockDialUnselectedContentColor = SportGreyLight,
                            selectorColor = SportOrange,
                            periodSelectorBorderColor = SportBorderLight,
                            periodSelectorSelectedContainerColor = SportOrange,
                            periodSelectorUnselectedContainerColor = SportSurface,
                            periodSelectorSelectedContentColor = SportWhite,
                            periodSelectorUnselectedContentColor = SportGreyLight,
                            timeSelectorSelectedContainerColor = SportOrange,
                            timeSelectorUnselectedContainerColor = SportSurface,
                            timeSelectorSelectedContentColor = SportWhite,
                            timeSelectorUnselectedContentColor = SportGreyLight
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val hour = timePickerState.hour
                        val minute = timePickerState.minute
                        val amPm = if (hour < 12) "AM" else "PM"
                        val displayHour = when {
                            hour == 0 -> 12
                            hour > 12 -> hour - 12
                            else -> hour
                        }
                        val timeStr = "$displayHour:${minute.toString().padStart(2, '0')} $amPm"
                        onTimeSelected(timeStr)
                        showPicker = false
                    }
                ) {
                    Text("OK", color = SportOrange, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("CANCEL", color = SportGreyLight)
                }
            }
        )
    }
}