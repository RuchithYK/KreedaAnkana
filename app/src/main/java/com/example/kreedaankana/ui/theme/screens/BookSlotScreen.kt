package com.example.kreedaankana.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kreedaankana.viewmodel.AuthViewModel
import com.example.kreedaankana.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSlotScreen(
    bookingViewModel: BookingViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    // sport dropdown state
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
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "📅 Reserve your ground slot",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // team name
            OutlinedTextField(
                value = bookingViewModel.teamName,
                onValueChange = { bookingViewModel.onTeamNameChange(it) },
                label = { Text("Team Name") },
                modifier = Modifier.fillMaxWidth()
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
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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

            // date
            OutlinedTextField(
                value = bookingViewModel.date,
                onValueChange = { bookingViewModel.onDateChange(it) },
                label = { Text("Date (e.g. 2026-05-10)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // start and end time side by side
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = bookingViewModel.startTime,
                    onValueChange = { bookingViewModel.onStartTimeChange(it) },
                    label = { Text("Start Time") },
                    placeholder = { Text("4:00 PM") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = bookingViewModel.endTime,
                    onValueChange = { bookingViewModel.onEndTimeChange(it) },
                    label = { Text("End Time") },
                    placeholder = { Text("6:00 PM") },
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
                    .height(52.dp)
            ) {
                Text(
                    text = "Confirm Booking",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}