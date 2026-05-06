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
import com.example.kreedaankana.viewmodel.ChallengeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeScreen(
    challengeViewModel: ChallengeViewModel,
    authViewModel: AuthViewModel
) {
    val challenges by challengeViewModel.challenges.collectAsState()
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
                text = "⚔️ Challenge Board",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            // toggle post challenge form
            Button(onClick = { showForm = !showForm }) {
                Text(if (showForm) "Cancel" else "Post Challenge")
            }
        }

        // post challenge form — shows only when showForm is true
        if (showForm) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = challengeViewModel.teamName,
                        onValueChange = { challengeViewModel.onTeamNameChange(it) },
                        label = { Text("Your Team Name") },
                        modifier = Modifier.fillMaxWidth()
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
                        placeholder = { Text("We are ready for a match this Sunday!") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = challengeViewModel.date,
                        onValueChange = { challengeViewModel.onDateChange(it) },
                        label = { Text("Preferred Date") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            challengeViewModel.addChallenge(authViewModel.userId)
                            showForm = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Post Challenge 🏆")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (challenges.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No challenges yet!\nBe the first to post one",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn {
                items(challenges) { challenge ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (challenge.sport == "Cricket") "🏏" else "🏐",
                                        fontSize = 24.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = challenge.teamName,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${challenge.sport} • ${challenge.date}",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                if (challenge.userId == authViewModel.userId) {
                                    IconButton(
                                        onClick = {
                                            challengeViewModel.deleteChallenge(challenge.id)
                                        }
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

                            Text(
                                text = "\"${challenge.message}\"",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}