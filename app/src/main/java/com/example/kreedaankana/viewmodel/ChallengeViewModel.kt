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

    // post challenge inputs
    var teamName by mutableStateOf("")
        private set
    var sport by mutableStateOf("Cricket")
        private set
    var message by mutableStateOf("")
        private set
    var date by mutableStateOf("")
        private set

    // reply input
    var replyInput by mutableStateOf("")
        private set

    // which challenge is being edited
    var editingChallenge by mutableStateOf<Challenge?>(null)
        private set

    fun onTeamNameChange(v: String) { teamName = v }
    fun onSportChange(v: String) { sport = v }
    fun onMessageChange(v: String) { message = v }
    fun onDateChange(v: String) { date = v }
    fun onReplyChange(v: String) { replyInput = v }

    fun startEditing(challenge: Challenge) {
        editingChallenge = challenge
        teamName = challenge.teamName
        sport = challenge.sport
        message = challenge.message
        date = challenge.date
    }

    fun cancelEditing() {
        editingChallenge = null
        teamName = ""
        sport = "Cricket"
        message = ""
        date = ""
    }

    fun addChallenge(userId: String) {
        if (teamName.isBlank() || message.isBlank()) return
        viewModelScope.launch {
            if (editingChallenge != null) {
                // UPDATE existing challenge
                repository.updateChallenge(
                    editingChallenge!!.copy(
                        teamName = teamName,
                        sport = sport,
                        message = message,
                        date = date
                    )
                )
                editingChallenge = null
            } else {
                // CREATE new challenge
                repository.addChallenge(
                    Challenge(
                        userId = userId,
                        teamName = teamName,
                        sport = sport,
                        message = message,
                        date = date,
                        status = "OPEN"
                    )
                )
            }
            // clear inputs
            teamName = ""
            message = ""
            date = ""
        }
    }

    // first team to accept wins — status changes to ACCEPTED
    fun acceptChallenge(
        challenge: Challenge,
        acceptingTeamName: String,
        acceptingUserId: String
    ) {
        // only accept if still OPEN
        if (challenge.status != "OPEN") return
        viewModelScope.launch {
            repository.updateChallenge(
                challenge.copy(
                    status = "ACCEPTED",
                    acceptedByTeam = acceptingTeamName,
                    acceptedByUserId = acceptingUserId
                )
            )
        }
    }

    // only accepted team can reply
    fun replyToChallenge(challenge: Challenge) {
        if (replyInput.isBlank()) return
        viewModelScope.launch {
            repository.updateChallenge(
                challenge.copy(reply = replyInput)
            )
            replyInput = ""
        }
    }

    fun deleteChallenge(challengeId: String) {
        viewModelScope.launch {
            repository.deleteChallenge(challengeId)
        }
    }
}