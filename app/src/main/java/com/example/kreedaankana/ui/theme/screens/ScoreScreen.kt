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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kreedaankana.ui.theme.components.DatePickerField
import com.example.kreedaankana.viewmodel.AuthViewModel
import com.example.kreedaankana.viewmodel.ScoreViewModel
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreen(
    scoreViewModel: ScoreViewModel,
    authViewModel: AuthViewModel
) {
    val scores by scoreViewModel.scores.collectAsState()
    val filteredScores = scoreViewModel.filteredScores
    var showForm by remember { mutableStateOf(false) }
    var sportExpanded by remember { mutableStateOf(false) }
    val sports = listOf("Cricket", "Volleyball")
    val filters = listOf("ALL", "Cricket", "Volleyball")

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
                text = "🏆 Score Wall",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = {
                    if (showForm) scoreViewModel.cancelEditing()
                    showForm = !showForm
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showForm) Color.Gray else Color(0xFFE65100)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (showForm) "Cancel" else "Post Score")
            }
        }

        // ── FILTER TABS ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A1A))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                FilterChip(
                    selected = scoreViewModel.selectedFilter == filter,
                    onClick = { scoreViewModel.onFilterChange(filter) },
                    label = {
                        Text(
                            text = when (filter) {
                                "Cricket" -> "🏏 Cricket"
                                "Volleyball" -> "🏐 Volleyball"
                                else -> "🏅 All"
                            },
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFE65100),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // ── POST SCORE FORM ──
            if (showForm || scoreViewModel.editingScore != null) {
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
                                text = if (scoreViewModel.editingScore != null)
                                    "✏️ Edit Score" else "📊 Post Match Result",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // team names row
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = scoreViewModel.team1,
                                    onValueChange = { scoreViewModel.onTeam1Change(it) },
                                    label = { Text("Team 1") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFFE65100),
                                        unfocusedBorderColor = Color.Gray
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = scoreViewModel.team2,
                                    onValueChange = { scoreViewModel.onTeam2Change(it) },
                                    label = { Text("Team 2") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFFE65100),
                                        unfocusedBorderColor = Color.Gray
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // scores row
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = scoreViewModel.score1,
                                    onValueChange = { scoreViewModel.onScore1Change(it) },
                                    label = { Text("Score 1") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = scoreViewModel.score2,
                                    onValueChange = { scoreViewModel.onScore2Change(it) },
                                    label = { Text("Score 2") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // sport dropdown
                            ExposedDropdownMenuBox(
                                expanded = sportExpanded,
                                onExpandedChange = { sportExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = scoreViewModel.sport,
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
                                                scoreViewModel.onSportChange(s)
                                                sportExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            DatePickerField(
                                label = "Match Date",
                                value = scoreViewModel.date,
                                onDateSelected = { scoreViewModel.onDateChange(it) },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    scoreViewModel.saveScore(authViewModel.userId)
                                    showForm = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE65100)
                                )
                            ) {
                                Text(
                                    text = if (scoreViewModel.editingScore != null)
                                        "Update Score ✅" else "Post Result 🏆",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // ── SCORES COUNT ──
            item {
                Text(
                    text = "${filteredScores.size} matches",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
                )
            }

            // ── SCORES LIST ──
            if (filteredScores.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🏆", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No scores yet!\nPost the first match result",
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredScores) { score ->
                    val won = score.score1 > score.score2

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

                            // sport + date header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${
                                        if (score.sport == "Cricket") "🏏" else "🏐"
                                    } ${score.sport} • ${score.date}",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )

                                // edit and delete — only for owner
                                if (score.userId == authViewModel.userId) {
                                    Row {
                                        IconButton(
                                            onClick = {
                                                scoreViewModel.startEditing(score)
                                                showForm = true
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                tint = Color(0xFF81C784),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                scoreViewModel.deleteScore(score.id)
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // SCOREBOARD
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // team 1
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = score.team1,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "${score.score1}",
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (score.score1 > score.score2)
                                            Color(0xFF81C784) else Color(0xFF757575)
                                    )
                                    if (score.score1 > score.score2) {
                                        Text(
                                            text = "🏆 WIN",
                                            color = Color(0xFF81C784),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // VS divider
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "VS",
                                        color = Color.Gray,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp
                                    )
                                }

                                // team 2
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = score.team2,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "${score.score2}",
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (score.score2 > score.score1)
                                            Color(0xFF81C784) else Color(0xFF757575)
                                    )
                                    if (score.score2 > score.score1) {
                                        Text(
                                            text = "🏆 WIN",
                                            color = Color(0xFF81C784),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}