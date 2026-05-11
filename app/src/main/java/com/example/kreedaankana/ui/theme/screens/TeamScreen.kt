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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kreedaankana.data.Team
import com.example.kreedaankana.data.UserProfile
import com.example.kreedaankana.ui.theme.*
import com.example.kreedaankana.viewmodel.TeamViewModel

@Composable
fun TeamScreen(
    teamViewModel: TeamViewModel,
    userProfile: UserProfile?
) {
    val allTeams by teamViewModel.allTeams.collectAsState()
    val currentTeam by teamViewModel.currentTeam.collectAsState()
    val teamScores by teamViewModel.teamScores.collectAsState()

    val userTeamId = userProfile?.teamId ?: ""

    // Auto-load user's team if joined
    LaunchedEffect(userTeamId) {
        if (userTeamId.isNotEmpty()) {
            teamViewModel.loadTeam(userTeamId)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(SportBlack)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // ── TOP BAR ──
            item {
                SportTopBar(title = "TEAM ARENA")
            }

            if (userTeamId.isEmpty() && currentTeam == null) {
                // ── NOT IN A TEAM VIEW ──
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "JOIN A TEAM",
                            fontSize = 12.sp,
                            color = SportOrange,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "START YOUR LEGACY",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = SportWhite,
                            lineHeight = 34.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // CREATE TEAM TOGGLE
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SportSurface, RoundedCornerShape(12.dp))
                                .border(1.dp, SportBorderLight, RoundedCornerShape(12.dp))
                                .clickable { teamViewModel.toggleCreateForm() }
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(SportOrange, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = SportWhite)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "CREATE NEW TEAM",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SportWhite
                                    )
                                    Text(
                                        text = "Lead your own squad",
                                        fontSize = 12.sp,
                                        color = SportGreyLight
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(visible = teamViewModel.showCreateForm) {
                            Column(
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .background(SportSurface2, RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                            ) {
                                SportFormTextField(
                                    value = teamViewModel.teamNameInput,
                                    onValueChange = { teamViewModel.onTeamNameChange(it) },
                                    label = "TEAM NAME",
                                    placeholder = "Enter team name"
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "PRIMARY SPORT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SportGreyLight,
                                    letterSpacing = 1.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("Cricket", "Football", "Basketball").forEach { sport ->
                                        val isSel = teamViewModel.sportInput == sport
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(
                                                    if (isSel) SportOrange else SportSurface,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { teamViewModel.onSportChange(sport) }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = sport,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSel) SportWhite else SportGreyLight
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { teamViewModel.createTeam {} },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = SportOrange),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = !teamViewModel.isLoading
                                ) {
                                    if (teamViewModel.isLoading) {
                                        CircularProgressIndicator(color = SportWhite, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("FOUND TEAM", fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "AVAILABLE TEAMS",
                            fontSize = 12.sp,
                            color = SportOrange,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                if (allTeams.isEmpty()) {
                    item { EmptyCard(message = "NO TEAMS FOUND. CREATE ONE!") }
                } else {
                    items(allTeams.filter { it.id != userTeamId }) { team ->
                        TeamListCard(team = team, onJoin = { teamViewModel.joinTeam(team) {} })
                    }
                }
            } else {
                // ── JOINED TEAM VIEW ──
                item {
                    currentTeam?.let { team ->
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Jersey/Hero card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(listOf(SportOrange, SportGradMid)),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(24.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(SportWhite.copy(alpha = 0.2f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = "🛡️", fontSize = 24.sp)
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = team.teamName.uppercase(),
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Black,
                                                color = SportWhite,
                                                letterSpacing = 1.sp
                                            )
                                            Text(
                                                text = "${team.sport.uppercase()} CLUB",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SportWhite.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                                        TeamHeroStat(label = "MEMBERS", value = team.members.size.toString())
                                        TeamHeroStat(label = "SINCE", value = team.createdAt.take(4))
                                        TeamHeroStat(label = "RANK", value = "#5")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // ROSTER
                            Text(
                                text = "TEAM ROSTER",
                                fontSize = 12.sp,
                                color = SportOrange,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                currentTeam?.members?.let { members ->
                    items(members) { member ->
                        MemberRow(member = member, isCaptain = member.userId == currentTeam?.captainId)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "TEAM PERFORMANCE",
                            fontSize = 12.sp,
                            color = SportOrange,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        if (teamScores.isEmpty()) {
                            EmptyCard(message = "NO RECENT MATCH RESULTS FOR THIS TEAM.")
                        } else {
                            teamScores.forEach { score ->
                                TeamScoreRow(score = score, teamName = currentTeam?.teamName ?: "")
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { teamViewModel.showLeaveConfirmation() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SportRedBg),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SportRed.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("LEAVE TEAM", color = SportRed, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }

        // LEAVE DIALOG
        if (teamViewModel.showLeaveDialog) {
            AlertDialog(
                onDismissRequest = { teamViewModel.hideLeaveConfirmation() },
                containerColor = SportSurface2,
                title = { Text("LEAVE TEAM?", color = SportWhite, fontWeight = FontWeight.Black) },
                text = { Text("You will lose access to team scores and roster.", color = SportGreyLight) },
                confirmButton = {
                    TextButton(onClick = { teamViewModel.leaveTeam(userTeamId) {} }) {
                        Text("LEAVE", color = SportRed, fontWeight = FontWeight.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { teamViewModel.hideLeaveConfirmation() }) {
                        Text("CANCEL", color = SportWhite)
                    }
                }
            )
        }
    }
}

@Composable
private fun TeamListCard(team: Team, onJoin: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(SportSurface, RoundedCornerShape(12.dp))
            .border(1.dp, SportBorderLight, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = team.teamName.uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = SportWhite
                )
                Text(
                    text = "${team.sport} · ${team.members.size} members",
                    fontSize = 12.sp,
                    color = SportGreyLight
                )
            }
            Button(
                onClick = onJoin,
                colors = ButtonDefaults.buttonColors(containerColor = SportSurface2),
                border = androidx.compose.foundation.BorderStroke(1.dp, SportOrange.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Text("JOIN", color = SportOrange, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun TeamHeroStat(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SportWhite.copy(alpha = 0.7f))
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = SportWhite)
    }
}

@Composable
private fun MemberRow(member: com.example.kreedaankana.data.TeamMember, isCaptain: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(SportSurface, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(SportSurface2, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = SportGreyLight, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = member.displayName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SportWhite)
            Text(text = "Joined ${member.joinedAt}", fontSize = 11.sp, color = SportGreyLight)
        }
        if (isCaptain) {
            Box(
                modifier = Modifier
                    .background(SportGold.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = "CAPTAIN", fontSize = 9.sp, fontWeight = FontWeight.Black, color = SportGold)
            }
        }
    }
}

@Composable
private fun TeamScoreRow(score: com.example.kreedaankana.data.Score, teamName: String) {
    val isWin = (score.team1 == teamName && score.score1 > score.score2) ||
                (score.team2 == teamName && score.score2 > score.score1)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(SportSurface2, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = if (score.team1 == teamName) "vs ${score.team2.uppercase()}" else "vs ${score.team1.uppercase()}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SportWhite
            )
            Text(text = score.date, fontSize = 11.sp, color = SportGreyLight)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${score.score1} - ${score.score2}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = SportWhite
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(if (isWin) SportGreen else SportRed, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = if (isWin) "W" else "L", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SportBlack)
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
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }
}
