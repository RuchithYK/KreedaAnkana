package com.example.kreedaankana.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kreedaankana.data.Challenge
import com.example.kreedaankana.repository.FirebaseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChallengeViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    val challenges = repository.getChallenges().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    var teamName by mutableStateOf("")
        private set
    var sport by mutableStateOf("Cricket")
        private set
    var message by mutableStateOf("")
        private set
    var date by mutableStateOf("")
        private set

    fun onTeamNameChange(v: String) { teamName = v }
    fun onSportChange(v: String) { sport = v }
    fun onMessageChange(v: String) { message = v }
    fun onDateChange(v: String) { date = v }

    fun addChallenge(userId: String) {
        if (teamName.isBlank() || message.isBlank()) return
        viewModelScope.launch {
            repository.addChallenge(
                Challenge(
                    userId = userId,
                    teamName = teamName,
                    sport = sport,
                    message = message,
                    date = date
                )
            )
            teamName = ""
            message = ""
            date = ""
        }
    }

    fun deleteChallenge(challengeId: String) {
        viewModelScope.launch {
            repository.deleteChallenge(challengeId)
        }
    }
}