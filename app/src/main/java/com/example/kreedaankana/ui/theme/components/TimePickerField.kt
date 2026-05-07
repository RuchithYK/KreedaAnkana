package com.example.kreedaankana.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

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
        initialHour = 16,   // default 4 PM
        initialMinute = 0,
        is24Hour = false    // show AM/PM
    )

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(
                    Icons.Default.Build,
                    contentDescription = "Pick Time",
                    tint = Color(0xFF81C784)
                )
            }
        },
        modifier = modifier
    )

    // time picker popup
    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            containerColor = Color(0xFF1A1A1A),
            title = {
                Text(
                    text = "Select $label",
                    color = Color.White
                )
            },
            text = {
                Box(contentAlignment = Alignment.Center) {
                    // clock face picker
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // convert hour/minute to readable string with AM/PM
                        val hour = timePickerState.hour
                        val minute = timePickerState.minute
                        val amPm = if (hour < 12) "AM" else "PM"
                        val displayHour = when {
                            hour == 0 -> 12
                            hour > 12 -> hour - 12
                            else -> hour
                        }
                        val timeStr = "$displayHour:${
                            minute.toString().padStart(2, '0')
                        } $amPm"
                        onTimeSelected(timeStr)
                        showPicker = false
                    }
                ) {
                    Text("OK", color = Color(0xFF81C784))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}