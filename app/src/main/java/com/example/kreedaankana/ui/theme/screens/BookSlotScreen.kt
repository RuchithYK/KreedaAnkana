package com.example.kreedaankana.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kreedaankana.ui.theme.components.DatePickerField
import com.example.kreedaankana.ui.theme.components.TimePickerField
import com.example.kreedaankana.viewmodel.AuthViewModel
import com.example.kreedaankana.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSlotScreen(
    bookingViewModel: BookingViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    var sportExpanded by remember { mutableStateOf(false) }
    val sports = listOf("Cricket", "Volleyball")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book a Slot") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0A0A0A)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = if (bookingViewModel.editingBooking != null)
                    "✏️ Edit Booking" else "📅 Reserve your ground slot",
                fontSize = 16.sp,
                color = Color(0xFF81C784),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // team name
            OutlinedTextField(
                value = bookingViewModel.teamName,
                onValueChange = { bookingViewModel.onTeamNameChange(it) },
                label = { Text("Team Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF81C784),
                    unfocusedLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // sport dropdown
            ExposedDropdownMenuBox(
                expanded = sportExpanded,
                onExpandedChange = { sportExpanded = it }
            ) {
                OutlinedTextField(
                    value = bookingViewModel.sport,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sport") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = sportExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF2E7D32),
                        unfocusedBorderColor = Color.Gray
                    )
                )
                ExposedDropdownMenu(
                    expanded = sportExpanded,
                    onDismissRequest = { sportExpanded = false }
                ) {
                    sports.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s) },
                            onClick = {
                                bookingViewModel.onSportChange(s)
                                sportExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // DATE PICKER — no more manual typing!
            DatePickerField(
                label = "Match Date",
                value = bookingViewModel.date,
                onDateSelected = { bookingViewModel.onDateChange(it) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // TIME PICKERS — no more manual typing!
            Row(modifier = Modifier.fillMaxWidth()) {
                TimePickerField(
                    label = "Start Time",
                    value = bookingViewModel.startTime,
                    onTimeSelected = { bookingViewModel.onStartTimeChange(it) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TimePickerField(
                    label = "End Time",
                    value = bookingViewModel.endTime,
                    onTimeSelected = { bookingViewModel.onEndTimeChange(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    bookingViewModel.addBooking(authViewModel.userId)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                )
            ) {
                Text(
                    text = if (bookingViewModel.editingBooking != null)
                        "Update Booking ✅" else "Confirm Booking ✅",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}