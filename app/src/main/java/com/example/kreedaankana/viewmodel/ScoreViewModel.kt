package com.example.kreedaankana.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kreedaankana.data.Score
import com.example.kreedaankana.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class ScoreViewModel : ViewModel() {

    private val repository = FirebaseRepository()
    private val firebaseAuth = FirebaseAuth.getInstance()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val authState = callbackFlow<com.google.firebase.auth.FirebaseUser?> {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    @OptIn(ExperimentalCoroutinesApi::class)
    val scores = authState.flatMapLatest { user ->
        if (user != null) repository.getScores() else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    // ✅ FIX: use a real MutableStateFlow so combine() reacts to filter changes
    private val _selectedFilter = MutableStateFlow("ALL")
    val selectedFilter: StateFlow<String> = _selectedFilter

    // ✅ FIX: filteredScores now truly reacts to both scores & filter changes
    val filteredScores: StateFlow<List<Score>> = combine(scores, _selectedFilter) { allScores, filter ->
        if (filter == "ALL") allScores
        else allScores.filter { it.sport == filter }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun onFilterChange(filter: String) {
        _selectedFilter.value = filter
    }

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

    var editingScore by mutableStateOf<Score?>(null)
        private set

    var isLoading by mutableStateOf(false)
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

    fun saveScore(userId: String, onComplete: () -> Unit = {}) {
        if (team1.isBlank() || team2.isBlank()) return
        isLoading = true
        viewModelScope.launch {
            try {
                if (editingScore != null) {
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
                onComplete()
            } finally {
                isLoading = false
            }
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
        sport = "Cricket"  // ✅ reset sport too
    }
}