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

    // filter — "ALL", "Cricket", "Volleyball"
    var selectedFilter by mutableStateOf("ALL")
        private set

    // filtered scores based on selected tab
    val filteredScores get() = if (selectedFilter == "ALL") {
        scores.value
    } else {
        scores.value.filter { it.sport == selectedFilter }
    }

    fun onFilterChange(filter: String) { selectedFilter = filter }

    // form inputs
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

    // which score is being edited
    var editingScore by mutableStateOf<Score?>(null)
        private set

    fun onTeam1Change(v: String) { team1 = v }
    fun onTeam2Change(v: String) { team2 = v }
    fun onScore1Change(v: String) { score1 = v }
    fun onScore2Change(v: String) { score2 = v }
    fun onSportChange(v: String) { sport = v }
    fun onDateChange(v: String) { date = v }

    fun startEditing(score: Score) {
        editingScore = score
        team1 = score.team1
        team2 = score.team2
        score1 = score.score1.toString()
        score2 = score.score2.toString()
        sport = score.sport
        date = score.date
    }

    fun cancelEditing() {
        editingScore = null
        clearInputs()
    }

    fun saveScore(userId: String) {
        if (team1.isBlank() || team2.isBlank()) return
        viewModelScope.launch {
            if (editingScore != null) {
                // UPDATE existing score
                repository.updateScore(
                    editingScore!!.copy(
                        team1 = team1,
                        team2 = team2,
                        score1 = score1.toIntOrNull() ?: 0,
                        score2 = score2.toIntOrNull() ?: 0,
                        sport = sport,
                        date = date
                    )
                )
                editingScore = null
            } else {
                // CREATE new score
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
            }
            clearInputs()
        }
    }

    fun deleteScore(scoreId: String) {
        viewModelScope.launch { repository.deleteScore(scoreId) }
    }

    private fun clearInputs() {
        team1 = ""
        team2 = ""
        score1 = ""
        score2 = ""
        date = ""
    }
}