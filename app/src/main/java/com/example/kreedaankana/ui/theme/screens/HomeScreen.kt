package com.example.kreedaankana.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kreedaankana.ui.theme.*
import com.example.kreedaankana.viewmodel.AuthViewModel
import com.example.kreedaankana.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    userName: String = ""
) {
    val recentBookings by homeViewModel.recentBookings.collectAsState()
    val recentChallenges by homeViewModel.recentChallenges.collectAsState()

    val bookingCount = recentBookings.size
    val openChallenges = recentChallenges.count { it.status == "OPEN" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportBlack)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // ── TOP BAR ──
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(SportSurface2, SportBlack)
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🏟️", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "KREEDA ANKANA",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    fontStyle = FontStyle.Italic,
                                    color = SportWhite,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Sports Ground Hub",
                                    fontSize = 11.sp,
                                    color = SportOrange,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Profile avatar
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(SportOrange, SportGradMid))
                                )
                                .clickable { navController.navigate("profile") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = SportWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Orange accent line at bottom of topbar
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

            // ── GREETING ──
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
                    Text(
                        text = "WELCOME BACK",
                        fontSize = 12.sp,
                        color = SportOrange,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = if (userName.isNotEmpty()) userName.uppercase() else "ATHLETE",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = SportWhite,
                        letterSpacing = 1.sp
                    )
                }
            }

            // ── LIVE STATS BAR ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(SportSurface2, RoundedCornerShape(12.dp))
                        .border(1.dp, SportBorderLight, RoundedCornerShape(12.dp))
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatChip(
                        icon = Icons.Default.CalendarToday,
                        value = bookingCount.toString(),
                        label = "BOOKINGS"
                    )
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(SportBorderLight))
                    StatChip(
                        icon = Icons.Default.FlashOn,
                        value = openChallenges.toString(),
                        label = "CHALLENGES",
                        valueColor = if (openChallenges > 0) SportGreen else SportGreyLight
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // ── GRID TILES ──
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SportHomeTile(
                            emoji = "📅",
                            label = "GROUND\nCALENDAR",
                            gradient = listOf(Color(0xFF1A1A3E), Color(0xFF0D0D28)),
                            accentColor = SportBlue,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate("calendar") }
                        )
                        SportHomeTile(
                            emoji = "⚡",
                            label = "CHALLENGE\nBOARD",
                            gradient = listOf(Color(0xFF3E1A00), Color(0xFF280D00)),
                            accentColor = SportOrange,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate("challenges") }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SportHomeTile(
                            emoji = "🏆",
                            label = "SCORE\nWALL",
                            gradient = listOf(Color(0xFF3E3000), Color(0xFF281E00)),
                            accentColor = SportGold,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate("scores") }
                        )
                        SportHomeTile(
                            emoji = "👥",
                            label = "MY\nTEAM",
                            gradient = listOf(Color(0xFF003E1A), Color(0xFF00280D)),
                            accentColor = SportGreen,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate("team") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ── QUICK BOOK BANNER ──
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(
                            Brush.horizontalGradient(listOf(SportOrange, SportOrangeDark)),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { navController.navigate("book_slot") }
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BOOK A SLOT",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = SportWhite,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Reserve your ground time now",
                                fontSize = 13.sp,
                                color = SportWhite.copy(alpha = 0.8f)
                            )
                        }
                        Text(text = "→", fontSize = 24.sp, color = SportWhite, fontWeight = FontWeight.Black)
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun SportHomeTile(
    emoji: String,
    label: String,
    gradient: List<Color>,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(0.9f)
            .background(Brush.linearGradient(gradient), RoundedCornerShape(16.dp))
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = emoji, fontSize = 36.sp)
            Column {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = SportWhite,
                    letterSpacing = 0.5.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .background(accentColor)
                )
            }
        }
    }
}

@Composable
private fun StatChip(
    icon: ImageVector,
    value: String,
    label: String,
    valueColor: Color = SportWhite
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SportOrange,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = valueColor)
            Text(text = label, fontSize = 10.sp, color = SportGreyLight, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ── SHARED UTILS — keep for backward compat ──
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = SportWhite,
        fontWeight = FontWeight.Black,
        fontSize = 16.sp,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
fun EmptyCard(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(SportSurface, RoundedCornerShape(8.dp))
            .border(1.dp, SportBorderLight, RoundedCornerShape(8.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = SportGreyLight, fontSize = 13.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}
