package com.example.kreedaankana.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kreedaankana.data.Team
import com.example.kreedaankana.data.UserProfile
import com.example.kreedaankana.viewmodel.TeamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(
    teamViewModel: TeamViewModel,
    userProfile: UserProfile?,
    onTeamUpdated: () -> Unit
) {
    val currentTeam by teamViewModel.currentTeam.collectAsState()
    val allTeams by teamViewModel.allTeams.collectAsState()
    val teamScores by teamViewModel.teamScores.collectAsState()

    // load team when profile is available
    LaunchedEffect(userProfile?.teamId) {
        if (!userProfile?.teamId.isNullOrEmpty()) {
            teamViewModel.loadTeam(userProfile!!.teamId)
        }
    }

    val hasTeam = !userProfile?.teamId.isNullOrEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        // ── TOP BAR ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A1A))
                .padding(20.dp)
        ) {
            Text(
                text = "🏟️ My Team",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (hasTeam && currentTeam != null) {
            // ── HAS TEAM VIEW ──
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // team header card
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
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (currentTeam!!.sport == "Cricket") "🏏" else "🏐",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentTeam!!.teamName,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = currentTeam!!.sport,
                                color = Color(0xFF81C784),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "👑 Captain: ${currentTeam!!.captainName}",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "📅 Created: ${currentTeam!!.createdAt}",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // members section
                item {
                    Text(
                        text = "👥 Members (${currentTeam!!.members.size})",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    )
                }

                items(currentTeam!!.members) { member ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A1A)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // avatar circle
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2E7D32)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = member.displayName
                                        .firstOrNull()?.uppercaseChar()
                                        ?.toString() ?: "?",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = member.displayName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Joined: ${member.joinedAt}",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }

                            // captain badge
                            if (member.userId == currentTeam!!.captainId) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFFD700).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "👑 Captain",
                                        color = Color(0xFFFFD700),
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // match history section
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "📊 Match History",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    )
                }

                if (teamScores.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1A1A1A)
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No matches played yet",
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                } else {
                    items(teamScores) { score ->
                        val isTeam1 = score.team1 == currentTeam!!.teamName
                        val myScore = if (isTeam1) score.score1 else score.score2
                        val oppScore = if (isTeam1) score.score2 else score.score1
                        val opponent = if (isTeam1) score.team2 else score.team1
                        val won = myScore > oppScore

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1A1A1A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "vs $opponent",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${score.sport} • ${score.date}",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "$myScore - $oppScore",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // WIN or LOSS badge
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (won)
                                        Color(0xFF1B5E20) else Color(0xFF8B0000)
                                ) {
                                    Text(
                                        text = if (won) "WON 🏆" else "LOST",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 6.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // leave team button
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { teamViewModel.showLeaveConfirmation() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B0000)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Leave Team",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

        } else {
            // ── NO TEAM VIEW ──
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🏟️", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "You're not in a team yet!",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Create a new team or join an existing one",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // create team button
                    Button(
                        onClick = { teamViewModel.toggleCreateForm() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (teamViewModel.showCreateForm)
                                "Cancel" else "Create New Team",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    // create team form
                    if (teamViewModel.showCreateForm) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1A1A1A)
                            )
                        ) {
                            var sportExpanded by remember { mutableStateOf(false) }
                            val sports = listOf("Cricket", "Volleyball")

                            Column(modifier = Modifier.padding(16.dp)) {
                                OutlinedTextField(
                                    value = teamViewModel.teamNameInput,
                                    onValueChange = { teamViewModel.onTeamNameChange(it) },
                                    label = { Text("Team Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFF2E7D32),
                                        unfocusedBorderColor = Color.Gray
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                ExposedDropdownMenuBox(
                                    expanded = sportExpanded,
                                    onExpandedChange = { sportExpanded = it }
                                ) {
                                    OutlinedTextField(
                                        value = teamViewModel.sportInput,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Sport") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults
                                                .TrailingIcon(expanded = sportExpanded)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(
                                                MenuAnchorType.PrimaryNotEditable
                                            ),
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
                                                    teamViewModel.onSportChange(s)
                                                    sportExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        teamViewModel.createTeam {
                                            onTeamUpdated()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2E7D32)
                                    )
                                ) {
                                    if (teamViewModel.isLoading) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Text("Create Team 🏟️")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // join team section
                    Text(
                        text = "🤝 Join Existing Team",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // list of all existing teams to join
                val joinableTeams = allTeams.filter { team ->
                    // don't show teams user is already in
                    team.members.none { it.userId == teamViewModel.userId }
                }

                if (joinableTeams.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1A1A1A)
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No teams available to join",
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                } else {
                    items(joinableTeams) { team ->
                        JoinTeamCard(
                            team = team,
                            onJoin = {
                                teamViewModel.joinTeam(team) {
                                    onTeamUpdated()
                                }
                            }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // leave team confirmation dialog
    if (teamViewModel.showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { teamViewModel.hideLeaveConfirmation() },
            containerColor = Color(0xFF1A1A1A),
            title = {
                Text("Leave Team?", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Are you sure you want to leave ${currentTeam?.teamName}?",
                    color = Color.Gray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        currentTeam?.let { team ->
                            teamViewModel.leaveTeam(team.id) {
                                onTeamUpdated()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Yes, Leave")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { teamViewModel.hideLeaveConfirmation() }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun JoinTeamCard(team: Team, onJoin: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (team.sport == "Cricket") "🏏" else "🏐",
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = team.teamName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${team.sport} • ${team.members.size} members",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
                Text(
                    text = "Captain: ${team.captainName}",
                    color = Color(0xFF81C784),
                    fontSize = 12.sp
                )
            }
            Button(
                onClick = onJoin,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Join", fontWeight = FontWeight.Bold)
            }
        }
    }
}