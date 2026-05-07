package com.example.kreedaankana.ui.theme.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // controls whether calendar popup is showing
    var showPicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    // when user taps the field → show calendar
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,  // user cant type manually
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Pick Date",
                    tint = Color(0xFF81C784)
                )
            }
        },
        modifier = modifier
    )

    // calendar popup
    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        // convert selected timestamp to readable date string
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val dateStr = formatter.format(Date(millis))
                            onDateSelected(dateStr)
                        }
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
        ) {
            DatePicker(state = datePickerState)
        }
    }
}