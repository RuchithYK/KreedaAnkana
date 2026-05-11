package com.example.kreedaankana.ui.theme.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kreedaankana.ui.theme.*
import com.example.kreedaankana.ui.theme.components.DatePickerField
import com.example.kreedaankana.ui.theme.components.TimePickerField
import com.example.kreedaankana.viewmodel.AuthViewModel
import com.example.kreedaankana.viewmodel.BookingViewModel
import kotlinx.coroutines.delay

data class SportOption(val label: String, val icon: ImageVector, val emoji: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSlotScreen(
    bookingViewModel: BookingViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val sportOptions = listOf(
        SportOption("Cricket",    Icons.Default.SportsCricket,    "🏏"),
        SportOption("Football",   Icons.Default.SportsSoccer,     "⚽"),
        SportOption("Basketball", Icons.Default.SportsBasketball, "🏀"),
        SportOption("Tennis",     Icons.Default.SportsTennis,     "🎾")
    )

    var showSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        bookingViewModel.clearConflictError()
        bookingViewModel.clearSaveSuccess()
    }

    // Auto-dismiss success after 1.5s then navigate back
    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(1200)
            navController.popBackStack()
        }
    }

    Scaffold(
        containerColor = SportBlack,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SportSurface2)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            bookingViewModel.cancelEditing()
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SportWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (bookingViewModel.editingBooking != null) "EDIT BOOKING" else "BOOK A SLOT",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = SportWhite,
                        letterSpacing = 1.sp
                    )
                }
                // Orange bottom line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.horizontalGradient(listOf(SportOrange, SportGradMid, Color.Transparent))
                        )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {

                // ── SUCCESS BANNER ──
                AnimatedVisibility(visible = showSuccess) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF001A08), RoundedCornerShape(10.dp))
                            .border(1.dp, SportGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SportGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Booking saved! Returning...", color = SportGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // ── CONFLICT ERROR ──
                if (bookingViewModel.conflictError.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SportRedBg, RoundedCornerShape(10.dp))
                            .border(1.dp, SportRed.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = SportRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = bookingViewModel.conflictError,
                                color = SportRed,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // ── SELECT SPORT ──
                Text(
                    text = "SELECT SPORT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = SportGreyLight,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    sportOptions.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            row.forEach { sport ->
                                val isSelected = bookingViewModel.sport == sport.label
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1.6f)
                                        .background(
                                            if (isSelected) Brush.linearGradient(listOf(SportOrange, SportOrangeDark))
                                            else Brush.linearGradient(listOf(SportSurface2, SportSurface2)),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) SportOrange else SportBorderLight,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            bookingViewModel.onSportChange(sport.label)
                                            bookingViewModel.clearConflictError()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = sport.emoji, fontSize = 28.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = sport.label.uppercase(),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isSelected) SportWhite else SportGreyLight,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── TEAM NAME ──
                Text(
                    text = "TEAM NAME",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = SportGreyLight,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bookingViewModel.teamName,
                    onValueChange = {
                        bookingViewModel.onTeamNameChange(it)
                        bookingViewModel.clearConflictError()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter team name", color = SportGreyMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SportWhite,
                        unfocusedTextColor = SportWhite,
                        cursorColor = SportOrange,
                        focusedBorderColor = SportOrange,
                        unfocusedBorderColor = SportBorderLight
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── DATE & TIME ──
                Text(
                    text = "DATE & TIME",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = SportGreyLight,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SportSurface, RoundedCornerShape(12.dp))
                        .border(1.dp, SportBorderLight, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text("SELECT DATE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SportGreyLight, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        DatePickerField(
                            label = "",
                            value = bookingViewModel.date,
                            onDateSelected = {
                                bookingViewModel.onDateChange(it)
                                bookingViewModel.clearConflictError()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("START TIME", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SportGreyLight, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                TimePickerField(
                                    label = "",
                                    value = bookingViewModel.startTime,
                                    onTimeSelected = {
                                        bookingViewModel.onStartTimeChange(it)
                                        bookingViewModel.clearConflictError()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("END TIME", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SportGreyLight, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                TimePickerField(
                                    label = "",
                                    value = bookingViewModel.endTime,
                                    onTimeSelected = {
                                        bookingViewModel.onEndTimeChange(it)
                                        bookingViewModel.clearConflictError()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ── BOOK NOW CTA ──
                Button(
                    onClick = {
                        bookingViewModel.saveBooking(authViewModel.userId) { saved ->
                            if (saved) showSuccess = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    enabled = !bookingViewModel.isSaving,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SportOrange,
                        contentColor = SportWhite,
                        disabledContainerColor = SportOrange.copy(alpha = 0.5f)
                    )
                ) {
                    if (bookingViewModel.isSaving) {
                        CircularProgressIndicator(
                            color = SportWhite,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = if (bookingViewModel.editingBooking != null) "⚡ UPDATE BOOKING" else "⚡ BOOK NOW",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
