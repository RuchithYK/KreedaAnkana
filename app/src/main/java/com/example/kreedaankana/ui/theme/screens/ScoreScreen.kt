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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kreedaankana.data.Score
import com.example.kreedaankana.ui.theme.*
import com.example.kreedaankana.ui.theme.components.DatePickerField
import com.example.kreedaankana.viewmodel.AuthViewModel
import com.example.kreedaankana.viewmodel.ScoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreen(
    scoreViewModel: ScoreViewModel,
    authViewModel: AuthViewModel
) {
    val filteredScores by scoreViewModel.filteredScores.collectAsState()
    val selectedFilter by scoreViewModel.selectedFilter.collectAsState()
    var showForm by remember { mutableStateOf(false) }
    var sportExpanded by remember { mutableStateOf(false) }
    val sports = listOf("Cricket", "Volleyball", "Football", "Basketball")
    val filters = listOf("ALL", "Cricket", "Volleyball", "Football", "Basketball")

    Box(modifier = Modifier.fillMaxSize().background(SportBlack)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // ── TOP BAR ──
            item {
                SportTopBar(title = "SCORE WALL") {
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
                            text = "RECENT",
                            fontSize = 12.sp,
                            color = SportOrange,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "MATCHES",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = SportWhite,
                            lineHeight = 34.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${filteredScores.size}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = SportWhite
                        )
                        Text(
                            text = "RESULTS",
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
                        val isSelected = selectedFilter == filter
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
                                .clickable { scoreViewModel.onFilterChange(filter) }
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
                AnimatedVisibility(visible = showForm || scoreViewModel.editingScore != null) {
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
                                text = if (scoreViewModel.editingScore != null) "EDIT RESULT" else "ADD RESULT",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = SportWhite,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                SportFormTextField(
                                    value = scoreViewModel.team1,
                                    onValueChange = { scoreViewModel.onTeam1Change(it) },
                                    label = "TEAM 1",
                                    placeholder = "Name",
                                    modifier = Modifier.weight(1f)
                                )
                                SportFormTextField(
                                    value = scoreViewModel.score1,
                                    onValueChange = { scoreViewModel.onScore1Change(it) },
                                    label = "SCORE",
                                    placeholder = "0",
                                    modifier = Modifier.width(60.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                SportFormTextField(
                                    value = scoreViewModel.team2,
                                    onValueChange = { scoreViewModel.onTeam2Change(it) },
                                    label = "TEAM 2",
                                    placeholder = "Name",
                                    modifier = Modifier.weight(1f)
                                )
                                SportFormTextField(
                                    value = scoreViewModel.score2,
                                    onValueChange = { scoreViewModel.onScore2Change(it) },
                                    label = "SCORE",
                                    placeholder = "0",
                                    modifier = Modifier.width(60.dp)
                                )
                            }
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
                                    value = scoreViewModel.sport,
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
                                                scoreViewModel.onSportChange(s)
                                                sportExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            DatePickerField(
                                label = "DATE",
                                value = scoreViewModel.date,
                                onDateSelected = { scoreViewModel.onDateChange(it) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = {
                                        scoreViewModel.cancelEditing()
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
                                        scoreViewModel.saveScore(authViewModel.userId) {
                                            showForm = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SportOrange),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = !scoreViewModel.isLoading
                                ) {
                                    if (scoreViewModel.isLoading) {
                                        CircularProgressIndicator(color = SportWhite, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("SAVE", fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── SCORE CARDS ──
            if (filteredScores.isEmpty()) {
                item {
                    EmptyCard(message = "NO MATCH RESULTS POSTED YET.")
                }
            } else {
                items(filteredScores) { score ->
                    SportScoreCard(
                        score = score,
                        currentUserId = authViewModel.userId,
                        onEdit = {
                            scoreViewModel.startEditing(score)
                            showForm = true
                        },
                        onDelete = { scoreViewModel.deleteScore(score.id) }
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
            Icon(Icons.Default.Add, contentDescription = "Add Result")
        }
    }
}

@Composable
private fun SportScoreCard(
    score: Score,
    currentUserId: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isOwner = score.userId == currentUserId

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(SportSurface, RoundedCornerShape(12.dp))
            .border(1.dp, SportBorderLight, RoundedCornerShape(12.dp))
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
                        text = score.sport.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = SportOrange,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = score.date,
                    fontSize = 11.sp,
                    color = SportGreyLight,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team 1
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = score.team1.uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = SportWhite,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = score.score1.toString(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = if (score.score1 >= score.score2) SportOrange else SportWhite
                    )
                }

                // VS
                Text(
                    text = "VS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = SportGreyMuted,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // Team 2
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = score.team2.uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = SportWhite,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = score.score2.toString(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = if (score.score2 >= score.score1) SportOrange else SportWhite
                    )
                }
            }

            if (isOwner) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SportBorderLight))
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = SportOrange, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SportRed, modifier = Modifier.size(20.dp))
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
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
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
            placeholder = { Text(placeholder, color = SportGreyMuted, fontSize = 12.sp) },
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
