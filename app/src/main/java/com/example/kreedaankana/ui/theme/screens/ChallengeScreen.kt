package com.example.kreedaankana.ui.theme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kreedaankana.data.Challenge
import com.example.kreedaankana.data.UserProfile
import com.example.kreedaankana.ui.theme.*
import com.example.kreedaankana.ui.theme.components.DatePickerField
import com.example.kreedaankana.viewmodel.AuthViewModel
import com.example.kreedaankana.viewmodel.ChallengeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeScreen(
    challengeViewModel: ChallengeViewModel,
    authViewModel: AuthViewModel,
    userProfile: UserProfile?
) {
    val challenges by challengeViewModel.challenges.collectAsState()
    var showForm by remember { mutableStateOf(false) }
    var sportExpanded by remember { mutableStateOf(false) }
    val sports = listOf("Cricket", "Volleyball", "Football", "Basketball")
    val filters = listOf("ALL", "Cricket", "Volleyball", "Football", "Basketball")

    val filteredChallenges = if (challengeViewModel.selectedFilter == "ALL") {
        challenges
    } else {
        challenges.filter { it.sport == challengeViewModel.selectedFilter }
    }

    Box(modifier = Modifier.fillMaxSize().background(SportBlack)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // ── TOP BAR ──
            item {
                SportTopBar(title = "CHALLENGE BOARD") {
                    // Navigate to profile
                }
            }

            // ── HEADER ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "ACTIVE",
                            fontSize = 12.sp,
                            color = SportOrange,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "BOARD",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = SportWhite,
                            lineHeight = 34.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${filteredChallenges.size}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = SportWhite
                        )
                        Text(
                            text = "CHALLENGES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportGreyLight,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // ── FILTER CHIPS ──
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { filter ->
                        val isSelected = challengeViewModel.selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) SportOrange else SportSurface2,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) SportOrange else SportBorderLight,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { challengeViewModel.onFilterChange(filter) }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = filter.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) SportWhite else SportGreyLight,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── POST FORM ──
            item {
                AnimatedVisibility(visible = showForm || challengeViewModel.editingChallenge != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(SportSurface, RoundedCornerShape(12.dp))
                            .border(1.dp, SportBorderLight, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = if (challengeViewModel.editingChallenge != null) "EDIT CHALLENGE" else "POST CHALLENGE",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = SportWhite,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            SportFormTextField(
                                value = challengeViewModel.teamName,
                                onValueChange = { challengeViewModel.onTeamNameChange(it) },
                                label = "TEAM NAME",
                                placeholder = "Your team name"
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "SPORT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = SportGreyLight,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            ExposedDropdownMenuBox(
                                expanded = sportExpanded,
                                onExpandedChange = { sportExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = challengeViewModel.sport,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sportExpanded) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = SportWhite,
                                        unfocusedTextColor = SportWhite,
                                        focusedBorderColor = SportOrange,
                                        unfocusedBorderColor = SportBorderLight
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = sportExpanded,
                                    onDismissRequest = { sportExpanded = false },
                                    containerColor = SportSurface2
                                ) {
                                    sports.forEach { s ->
                                        DropdownMenuItem(
                                            text = { Text(s, color = SportWhite) },
                                            onClick = {
                                                challengeViewModel.onSportChange(s)
                                                sportExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            SportFormTextField(
                                value = challengeViewModel.message,
                                onValueChange = { challengeViewModel.onMessageChange(it) },
                                label = "MESSAGE",
                                placeholder = "Challenge message"
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            DatePickerField(
                                label = "PREFERRED DATE",
                                value = challengeViewModel.date,
                                onDateSelected = { challengeViewModel.onDateChange(it) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = {
                                        challengeViewModel.cancelEditing()
                                        showForm = false
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SportSurface2),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("CANCEL", color = SportWhite)
                                }
                                Button(
                                    onClick = {
                                        challengeViewModel.addChallenge(authViewModel.userId) {
                                            showForm = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SportOrange),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = !challengeViewModel.isLoading
                                ) {
                                    if (challengeViewModel.isLoading) {
                                        CircularProgressIndicator(color = SportWhite, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("SUBMIT", fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── CHALLENGE CARDS ──
            if (filteredChallenges.isEmpty()) {
                item {
                    EmptyCard(message = "NO CHALLENGES FOUND\nBE THE FIRST TO POST ONE!")
                }
            } else {
                items(filteredChallenges) { challenge ->
                    SportChallengeCard(
                        challenge = challenge,
                        currentUserId = authViewModel.userId,
                        userProfile = userProfile,
                        replyInput = challengeViewModel.replyInput,
                        onReplyChange = { challengeViewModel.onReplyChange(it) },
                        onAccept = {
                            challengeViewModel.acceptChallenge(
                                challenge = challenge,
                                acceptingTeamName = userProfile?.teamName ?: "",
                                acceptingUserId = authViewModel.userId
                            )
                        },
                        onReply = { challengeViewModel.replyToChallenge(challenge) },
                        onEdit = {
                            challengeViewModel.startEditing(challenge)
                            showForm = true
                        },
                        onDelete = { challengeViewModel.deleteChallenge(challenge.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }

        // ── FAB ──
        FloatingActionButton(
            onClick = { showForm = !showForm },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .padding(bottom = 70.dp),
            containerColor = SportOrange,
            contentColor = SportWhite,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Challenge")
        }
    }
}

@Composable
private fun SportChallengeCard(
    challenge: Challenge,
    currentUserId: String,
    userProfile: UserProfile?,
    replyInput: String,
    onReplyChange: (String) -> Unit,
    onAccept: () -> Unit,
    onReply: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showReplyBox by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val isOwner = challenge.userId == currentUserId
    val isAcceptedTeam = challenge.acceptedByUserId == currentUserId
    val canAccept = challenge.status == "OPEN" && !isOwner

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(SportSurface, RoundedCornerShape(12.dp))
            .border(1.dp, SportBorderLight, RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(SportSurface2, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = challenge.sport.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = SportOrange,
                        letterSpacing = 1.sp
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (challenge.status == "OPEN") SportGreen else SportBlue,
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = challenge.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (challenge.status == "OPEN") SportGreen else SportBlue,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = challenge.teamName.uppercase(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = SportWhite
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = challenge.message,
                fontSize = 14.sp,
                color = SportGreyLight
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = SportOrange, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = challenge.date.ifEmpty { "DATE TBD" },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportWhite
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SportBorderLight))
                Spacer(modifier = Modifier.height(12.dp))

                if (challenge.status == "ACCEPTED" && challenge.acceptedByTeam.isNotEmpty()) {
                    Text(
                        text = "✓ ACCEPTED BY: ${challenge.acceptedByTeam.uppercase()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = SportBlue
                    )
                }

                if (challenge.reply.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SportSurface2, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "💬 ${challenge.reply}",
                            fontSize = 13.sp,
                            color = SportGreyLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (canAccept) {
                        Button(
                            onClick = onAccept,
                            colors = ButtonDefaults.buttonColors(containerColor = SportOrange),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("ACCEPT", fontWeight = FontWeight.Black)
                        }
                    }
                    if (isAcceptedTeam && challenge.status == "ACCEPTED") {
                        Button(
                            onClick = { showReplyBox = !showReplyBox },
                            colors = ButtonDefaults.buttonColors(containerColor = SportSurface2),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("REPLY", color = SportWhite)
                        }
                    }
                    if (isOwner) {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = SportOrange)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SportRed)
                        }
                    }
                }

                if (showReplyBox) {
                    Spacer(modifier = Modifier.height(12.dp))
                    SportFormTextField(
                        value = replyInput,
                        onValueChange = onReplyChange,
                        label = "YOUR REPLY",
                        placeholder = "Type your reply..."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            onReply()
                            showReplyBox = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SportBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("SEND REPLY", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun SportFormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String
) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = SportGreyLight,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = SportGreyMuted) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = SportWhite,
                unfocusedTextColor = SportWhite,
                cursorColor = SportOrange,
                focusedBorderColor = SportOrange,
                unfocusedBorderColor = SportBorderLight
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}
