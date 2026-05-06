package com.example.kreedaankana.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kreedaankana.viewmodel.AuthViewModel
import com.example.kreedaankana.viewmodel.ScoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreen(
    scoreViewModel: ScoreViewModel,
    authViewModel: AuthViewModel
) {
    val scores by scoreViewModel.scores.collectAsState()
    var showForm by remember { mutableStateOf(false) }
    var sportExpanded by remember { mutableStateOf(false) }
    val sports = listOf("Cricket", "Volleyball")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🏆 Score Wall",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = { showForm = !showForm }) {
                Text(if (showForm) "Cancel" else "Post Score")
            }
        }

        if (showForm) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = scoreViewModel.team1,
                            onValueChange = { scoreViewModel.onTeam1Change(it) },
                            label = { Text("Team 1") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = scoreViewModel.team2,
                            onValueChange = { scoreViewModel.onTeam2Change(it) },
                            label = { Text("Team 2") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = scoreViewModel.score1,
                            onValueChange = { scoreViewModel.onScore1Change(it) },
                            label = { Text("Score 1") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = scoreViewModel.score2,
                            onValueChange = { scoreViewModel.onScore2Change(it) },
                            label = { Text("Score 2") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

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
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
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

                    OutlinedTextField(
                        value = scoreViewModel.date,
                        onValueChange = { scoreViewModel.onDateChange(it) },
                        label = { Text("Match Date") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            scoreViewModel.addScore(authViewModel.userId)
                            showForm = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Post Result 🏆")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (scores.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No scores yet!\nPost the first match result",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn {
                items(scores) { score ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${if (score.sport == "Cricket") "🏏" else "🏐"} ${score.sport} • ${score.date}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                if (score.userId == authViewModel.userId) {
                                    IconButton(
                                        onClick = { scoreViewModel.deleteScore(score.id) }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // score display
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = score.team1,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "${score.score1}",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (score.score1 > score.score2)
                                            Color(0xFF2E7D32) else Color.Gray
                                    )
                                }
                                Text(
                                    text = "VS",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    fontSize = 18.sp
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = score.team2,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "${score.score2}",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (score.score2 > score.score1)
                                            Color(0xFF2E7D32) else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}