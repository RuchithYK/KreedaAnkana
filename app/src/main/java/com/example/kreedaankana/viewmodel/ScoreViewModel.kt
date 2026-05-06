package com.example.kreedaankana.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kreedaankana.data.Score
import com.example.kreedaankana.repository.FirebaseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScoreViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    val scores = repository.getScores().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    var team1 by mutableStateOf("")
        private set
    var team2 by mutableStateOf("")
        private set
    var score1 by mutableStateOf("")
        private set
    var score2 by mutableStateOf("")
        private set
    var sport by mutableStateOf("Cricket")
        private set
    var date by mutableStateOf("")
        private set

    fun onTeam1Change(v: String) { team1 = v }
    fun onTeam2Change(v: String) { team2 = v }
    fun onScore1Change(v: String) { score1 = v }
    fun onScore2Change(v: String) { score2 = v }
    fun onSportChange(v: String) { sport = v }
    fun onDateChange(v: String) { date = v }

    fun addScore(userId: String) {
        if (team1.isBlank() || team2.isBlank()) return
        viewModelScope.launch {
            repository.addScore(
                Score(
                    userId = userId,
                    team1 = team1,
                    team2 = team2,
                    score1 = score1.toIntOrNull() ?: 0,
                    score2 = score2.toIntOrNull() ?: 0,
                    sport = sport,
                    date = date
                )
            )
            team1 = ""
            team2 = ""
            score1 = ""
            score2 = ""
            date = ""
        }
    }

    fun deleteScore(scoreId: String) {
        viewModelScope.launch {
            repository.deleteScore(scoreId)
        }
    }
}