package com.example.kreedaankana.ui.theme.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kreedaankana.ui.theme.*
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
    var showPicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = if (label.isNotEmpty()) { { Text(label, color = SportGreyLight, fontSize = 12.sp, fontWeight = FontWeight.Bold) } } else null,
        placeholder = { Text("Select Date", color = SportGreyMuted) },
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Pick Date",
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
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val dateStr = formatter.format(Date(millis))
                            onDateSelected(dateStr)
                        }
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
            },
            colors = DatePickerDefaults.colors(
                containerColor = SportSurface2
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    titleContentColor = SportWhite,
                    headlineContentColor = SportWhite,
                    weekdayContentColor = SportGreyLight,
                    dayContentColor = SportWhite,
                    selectedDayContainerColor = SportOrange,
                    selectedDayContentColor = SportWhite,
                    todayContentColor = SportOrange,
                    todayDateBorderColor = SportOrange
                )
            )
        }
    }
}