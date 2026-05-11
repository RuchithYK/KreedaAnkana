package com.example.kreedaankana.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kreedaankana.ui.theme.*
import com.example.kreedaankana.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    val userProfile by profileViewModel.userProfile.collectAsState()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(SportBlack)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // ── TOP BAR ──
            SportTopBar(title = "PLAYER PROFILE")

            // ── PROFILE HEADER ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(SportSurface2, SportBlack))
                    )
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar with gradient border
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(SportOrange, SportGradMid))
                            )
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileViewModel.photoUrl.isNotEmpty()) {
                            AsyncImage(
                                model = profileViewModel.photoUrl,
                                contentDescription = "Profile Photo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(SportSurface2),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = SportGreyLight,
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = profileViewModel.displayName.uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = SportWhite,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = profileViewModel.email,
                        fontSize = 14.sp,
                        color = SportGreyLight
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (userProfile?.teamName?.isNotEmpty() == true) {
                        Box(
                            modifier = Modifier
                                .background(SportOrange.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                                .border(1.dp, SportOrange.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🛡️", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = userProfile?.teamName?.uppercase() ?: "",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SportOrange,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            // ── PLAYER STATS ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(SportSurface2, RoundedCornerShape(12.dp))
                    .border(1.dp, SportBorderLight, RoundedCornerShape(12.dp))
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(label = "RANK", value = "#12")
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(SportBorderLight))
                ProfileStat(label = "GAMES", value = "24")
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(SportBorderLight))
                ProfileStat(label = "WINS", value = "18")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── EDIT TEAM ──
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "TEAM SETTINGS",
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
                    if (profileViewModel.isEditing) {
                        Column {
                            OutlinedTextField(
                                value = profileViewModel.teamNameInput,
                                onValueChange = { profileViewModel.onTeamNameChange(it) },
                                label = { Text("TEAM NAME", color = SportGreyLight, fontSize = 10.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = SportWhite,
                                    unfocusedTextColor = SportWhite,
                                    focusedBorderColor = SportOrange,
                                    unfocusedBorderColor = SportBorderLight
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { profileViewModel.cancelEditing() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SportSurface2),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("CANCEL", color = SportWhite)
                                }
                                Button(
                                    onClick = { profileViewModel.saveTeamName() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SportOrange),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = !profileViewModel.isSaving
                                ) {
                                    if (profileViewModel.isSaving) {
                                        CircularProgressIndicator(color = SportWhite, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("SAVE", fontWeight = FontWeight.Black)
                                    }
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
                                    text = "TEAM NAME",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SportGreyLight
                                )
                                Text(
                                    text = userProfile?.teamName?.ifEmpty { "NOT JOINED" } ?: "NOT JOINED",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SportWhite
                                )
                            }
                            IconButton(onClick = { profileViewModel.startEditing(userProfile?.teamName ?: "") }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = SportOrange)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── ACTIONS ──
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                ProfileActionButton(
                    icon = Icons.Default.Logout,
                    text = "LOGOUT ACCOUNT",
                    color = SportWhite,
                    bgColor = SportSurface2,
                    onClick = {
                        profileViewModel.signOut()
                        onLogout()
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileActionButton(
                    icon = Icons.Default.DeleteForever,
                    text = "DELETE DATA & ACCOUNT",
                    color = SportRed,
                    bgColor = SportRedBg,
                    onClick = { profileViewModel.showDeleteConfirmation() }
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // ── DELETE DIALOG ──
        if (profileViewModel.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { profileViewModel.hideDeleteConfirmation() },
                containerColor = SportSurface2,
                title = {
                    Text(
                        "DELETE ACCOUNT?",
                        color = SportRed,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic
                    )
                },
                text = {
                    Text(
                        "This will permanently erase all your bookings, scores, and team data. This action cannot be undone.",
                        color = SportGreyLight
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            profileViewModel.deleteAccount {
                                onLogout()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SportRed),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !profileViewModel.isDeleting
                    ) {
                        if (profileViewModel.isDeleting) {
                            CircularProgressIndicator(color = SportWhite, modifier = Modifier.size(20.dp))
                        } else {
                            Text("DELETE EVERYTHING", fontWeight = FontWeight.Black)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { profileViewModel.hideDeleteConfirmation() }) {
                        Text("CANCEL", color = SportWhite)
                    }
                }
            )
        }
    }
}

@Composable
private fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = SportGreyLight,
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = SportWhite
        )
    }
}

@Composable
private fun ProfileActionButton(
    icon: ImageVector,
    text: String,
    color: Color,
    bgColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = color,
                letterSpacing = 1.sp
            )
        }
    }
}
