package com.example.kreedaankana.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kreedaankana.data.Challenge
import com.example.kreedaankana.data.UserProfile
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
    val sports = listOf("Cricket", "Volleyball")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        // ── TOP BAR ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A1A))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚔️ Challenge Board",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = {
                    if (showForm) challengeViewModel.cancelEditing()
                    showForm = !showForm
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showForm) Color.Gray else Color(0xFF6A1B9A)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (showForm) "Cancel" else "Post Challenge")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            // ── POST CHALLENGE FORM ──
            if (showForm || challengeViewModel.editingChallenge != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A1A)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (challengeViewModel.editingChallenge != null)
                                    "✏️ Edit Challenge" else "📢 Post a Challenge",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = challengeViewModel.teamName,
                                onValueChange = { challengeViewModel.onTeamNameChange(it) },
                                label = { Text("Your Team Name") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF6A1B9A),
                                    unfocusedBorderColor = Color.Gray
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            ExposedDropdownMenuBox(
                                expanded = sportExpanded,
                                onExpandedChange = { sportExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = challengeViewModel.sport,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Sport") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = sportExpanded
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
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
                                                challengeViewModel.onSportChange(s)
                                                sportExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = challengeViewModel.message,
                                onValueChange = { challengeViewModel.onMessageChange(it) },
                                label = { Text("Challenge Message") },
                                placeholder = {
                                    Text("We are ready for a match this Sunday!")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF6A1B9A),
                                    unfocusedBorderColor = Color.Gray
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            DatePickerField(
                                label = "Preferred Date",
                                value = challengeViewModel.date,
                                onDateSelected = { challengeViewModel.onDateChange(it) },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    challengeViewModel.addChallenge(authViewModel.userId)
                                    showForm = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6A1B9A)
                                )
                            ) {
                                Text(
                                    text = if (challengeViewModel.editingChallenge != null)
                                        "Update Challenge ✅" else "Post Challenge 🏆",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // ── CHALLENGES LIST ──
            if (challenges.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "⚔️", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No challenges yet!\nBe the first to post one",
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(challenges) { challenge ->
                    ChallengeCard(
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
                        onReply = {
                            challengeViewModel.replyToChallenge(challenge)
                        },
                        onEdit = {
                            challengeViewModel.startEditing(challenge)
                            showForm = true
                        },
                        onDelete = {
                            challengeViewModel.deleteChallenge(challenge.id)
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun ChallengeCard(
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

    val isOwner = challenge.userId == currentUserId
    val isAcceptedTeam = challenge.acceptedByUserId == currentUserId
    val canAccept = challenge.status == "OPEN" &&
            !isOwner &&
            challenge.userId != currentUserId

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── CARD HEADER ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (challenge.sport == "Cricket") "🏏" else "🏐",
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = challenge.teamName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${challenge.sport} • ${challenge.date}",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }

                // STATUS BADGE — OPEN or ACCEPTED
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (challenge.status == "OPEN")
                        Color(0xFF1565C0) else Color(0xFF2E7D32)
                ) {
                    Text(
                        text = challenge.status,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 5.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // challenge message
            Text(
                text = "\"${challenge.message}\"",
                color = Color(0xFFE0E0E0),
                fontSize = 14.sp
            )

            // show who accepted
            if (challenge.status == "ACCEPTED" &&
                challenge.acceptedByTeam.isNotEmpty()
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1B5E20).copy(alpha = 0.3f)
                ) {
                    Text(
                        text = "✅ Accepted by: ${challenge.acceptedByTeam}",
                        color = Color(0xFF81C784),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        )
                    )
                }
            }

            // show reply if exists
            if (challenge.reply.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF6A1B9A).copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 8.dp
                        )
                    ) {
                        Text(
                            text = "💬 ",
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${challenge.acceptedByTeam}: ${challenge.reply}",
                            color = Color(0xFFCE93D8),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── ACTION BUTTONS ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                // ACCEPT BUTTON — only shown when OPEN and not owner
                if (canAccept) {
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    ) {
                        Text(
                            text = "⚡ Accept",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // REPLY BUTTON — only for accepted team
                if (isAcceptedTeam && challenge.status == "ACCEPTED") {
                    OutlinedButton(
                        onClick = { showReplyBox = !showReplyBox },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    ) {
                        Text(
                            text = "💬 Reply",
                            color = Color(0xFFCE93D8),
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // EDIT and DELETE — only for owner
                if (isOwner) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // REPLY INPUT BOX
            if (showReplyBox && isAcceptedTeam) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = replyInput,
                    onValueChange = onReplyChange,
                    label = { Text("Your reply...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6A1B9A),
                        unfocusedBorderColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        onReply()
                        showReplyBox = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6A1B9A)
                    )
                ) {
                    Text("Send Reply 💬")
                }
            }
        }
    }
}