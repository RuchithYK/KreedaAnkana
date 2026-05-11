package com.example.kreedaankana.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kreedaankana.data.Booking
import com.example.kreedaankana.ui.theme.*
import com.example.kreedaankana.viewmodel.AuthViewModel
import com.example.kreedaankana.viewmodel.BookingViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(
    bookingViewModel: BookingViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val bookings by bookingViewModel.bookings.collectAsState()

    // Calendar state
    val calendar = remember { Calendar.getInstance() }
    var displayedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var displayedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(
        Calendar.getInstance().apply {
            set(Calendar.MONTH, displayedMonth)
            set(Calendar.YEAR, displayedYear)
            set(Calendar.DAY_OF_MONTH, 1)
        }.time
    ).uppercase()

    val daysInMonth = remember(displayedMonth, displayedYear) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, displayedMonth)
        cal.set(Calendar.YEAR, displayedYear)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val days = mutableListOf<Pair<Int, String>>()
        val daysCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dayLabels = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
        for (d in 1..daysCount) {
            cal.set(Calendar.DAY_OF_MONTH, d)
            days.add(d to dayLabels[cal.get(Calendar.DAY_OF_WEEK) - 1])
        }
        days
    }

    // ✅ Fixed date format — must match DatePickerField's dd/MM/yyyy output
    val selectedDateStr = remember(selectedDay, displayedMonth, displayedYear) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.set(displayedYear, displayedMonth, selectedDay)
        sdf.format(cal.time)
    }

    val dayBookings = remember(bookings, selectedDateStr) {
        bookings.filter { it.date == selectedDateStr }
    }

    // Check which days have bookings for dot indicators
    val daysWithBookings = remember(bookings, displayedMonth, displayedYear) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        bookings.mapNotNull { booking ->
            try {
                val cal = Calendar.getInstance()
                cal.time = sdf.parse(booking.date) ?: return@mapNotNull null
                if (cal.get(Calendar.MONTH) == displayedMonth &&
                    cal.get(Calendar.YEAR) == displayedYear
                ) cal.get(Calendar.DAY_OF_MONTH) else null
            } catch (e: Exception) { null }
        }.toSet()
    }

    val hours = (6..22).map { h -> String.format("%02d:00", h) }

    Box(modifier = Modifier.fillMaxSize().background(SportBlack)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // ── TOP BAR ──
            item {
                SportTopBar(title = "GROUND CALENDAR") {
                    navController.navigate("profile")
                }
            }

            // ── MONTH NAVIGATION ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = monthName,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = SportWhite
                        )
                        Text(
                            text = "${bookings.size} bookings this period",
                            fontSize = 12.sp,
                            color = SportGreyLight
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MonthNavButton(icon = Icons.Default.ChevronLeft) {
                            if (displayedMonth == 0) { displayedMonth = 11; displayedYear-- }
                            else displayedMonth--
                            selectedDay = 1
                        }
                        MonthNavButton(icon = Icons.Default.ChevronRight) {
                            if (displayedMonth == 11) { displayedMonth = 0; displayedYear++ }
                            else displayedMonth++
                            selectedDay = 1
                        }
                    }
                }
            }

            // ── DAY STRIP ──
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(daysInMonth) { (day, dayLabel) ->
                        val isSelected = day == selectedDay
                        val hasBooking = day in daysWithBookings
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(52.dp)
                                    .background(
                                        if (isSelected) Brush.verticalGradient(listOf(SportOrange, SportOrangeDark))
                                        else Brush.verticalGradient(listOf(SportSurface2, SportSurface2)),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) SportOrange else SportBorderLight,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedDay = day }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = dayLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) SportWhite else SportGreyLight,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = day.toString(),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) SportWhite else SportWhite
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            // Dot indicator for days with bookings
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(
                                        if (hasBooking) SportOrange else Color.Transparent,
                                        RoundedCornerShape(50)
                                    )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // ── SELECTED DATE HEADER ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = selectedDateStr,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = SportOrange
                        )
                        Text(
                            text = if (dayBookings.isEmpty()) "All slots available" else "${dayBookings.size} slot(s) booked",
                            fontSize = 12.sp,
                            color = SportGreyLight
                        )
                    }
                    // Book button
                    Box(
                        modifier = Modifier
                            .background(SportOrange, RoundedCornerShape(8.dp))
                            .clickable { navController.navigate("book_slot") }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "+ BOOK",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = SportWhite
                        )
                    }
                }
            }

            // ── TIME SLOTS ──
            items(hours) { hourStr ->
                val slotHour = hourStr.substring(0, 2).toIntOrNull() ?: 0
                val slotStartAmPm = if (slotHour < 12) "${hourStr.substring(0,5)} AM"
                else if (slotHour == 12) "12:00 PM"
                else "${String.format("%02d", slotHour - 12)}:00 PM"
                val slotEndHour = slotHour + 1
                val slotEndAmPm = if (slotEndHour < 12) "${String.format("%02d", slotEndHour)}:00 AM"
                else if (slotEndHour == 12) "12:00 PM"
                else "${String.format("%02d", slotEndHour - 12)}:00 PM"

                val slotBooking = dayBookings.firstOrNull { booking ->
                    com.example.kreedaankana.ui.theme.components.timesOverlap(
                        newStart = slotStartAmPm,
                        newEnd = slotEndAmPm,
                        existingStart = booking.startTime,
                        existingEnd = booking.endTime
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.Top
                ) {
                    // Hour label
                    Text(
                        text = hourStr,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportGreyLight,
                        modifier = Modifier.width(52.dp).padding(top = 14.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    val isBooked = slotBooking != null
                    val isCurrentUserBooking = slotBooking?.userId == authViewModel.userId

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isBooked) {
                                    Brush.horizontalGradient(listOf(SportRedBg, Color(0xFF1A0008)))
                                } else {
                                    Brush.horizontalGradient(listOf(SportSurface, SportSurface))
                                },
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = if (isBooked) 1.dp else 1.dp,
                                color = if (isBooked) SportRed.copy(alpha = 0.5f) else SportBorderLight,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .then(
                                if (isBooked && isCurrentUserBooking)
                                    Modifier.clickable { bookingViewModel.startEditing(slotBooking!!); navController.navigate("book_slot") }
                                else Modifier
                            )
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        if (isBooked) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = slotBooking!!.teamName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SportWhite
                                    )
                                    Text(
                                        text = "${slotBooking.sport} · ${slotBooking.startTime} – ${slotBooking.endTime}",
                                        fontSize = 12.sp,
                                        color = SportRed.copy(alpha = 0.8f)
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = getSportEmoji(slotBooking!!.sport),
                                        fontSize = 20.sp
                                    )
                                    if (isCurrentUserBooking) {
                                        // Edit and delete for own bookings
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = SportOrange,
                                            modifier = Modifier.size(18.dp).clickable {
                                                bookingViewModel.startEditing(slotBooking)
                                                navController.navigate("book_slot")
                                            }
                                        )
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = SportRed,
                                            modifier = Modifier.size(18.dp).clickable {
                                                bookingViewModel.deleteBooking(slotBooking.id)
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "AVAILABLE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SportGreen,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Main Turf · Full Field",
                                        fontSize = 12.sp,
                                        color = SportGreyMuted
                                    )
                                }
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Book",
                                    tint = SportGreyMuted,
                                    modifier = Modifier.size(18.dp).clickable { navController.navigate("book_slot") }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

private fun getSportEmoji(sport: String): String = when (sport.lowercase()) {
    "cricket" -> "🏏"
    "football" -> "⚽"
    "basketball" -> "🏀"
    "tennis" -> "🎾"
    "volleyball" -> "🏐"
    else -> "🏅"
}

// ── SHARED TOP BAR for screens ──
@Composable
fun SportTopBar(title: String, onProfileClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SportSurface2)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = SportWhite,
                letterSpacing = 1.sp
            )
            if (onProfileClick != null) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(
                            Brush.linearGradient(listOf(SportOrange, SportGradMid))
                        )
                        .clickable(onClick = onProfileClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = SportWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
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

@Composable
private fun MonthNavButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .background(SportSurface2, RoundedCornerShape(8.dp))
            .border(1.dp, SportBorderLight, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = SportWhite, modifier = Modifier.size(20.dp))
    }
}
