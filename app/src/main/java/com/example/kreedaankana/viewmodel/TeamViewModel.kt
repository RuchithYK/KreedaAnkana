package com.example.kreedaankana.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kreedaankana.data.Team
import com.example.kreedaankana.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TeamViewModel : ViewModel() {

    private val repository = FirebaseRepository()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    val userId = currentUser?.uid ?: ""
    val displayName = currentUser?.displayName ?: ""

    // all teams from firestore
    val allTeams = repository.getAllTeams().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // current user's team — loaded after we know teamId
    private val _currentTeam = MutableStateFlow<Team?>(null)
    val currentTeam: StateFlow<Team?> = _currentTeam

    // team match history
    val teamScores = repository.getTeamScores(displayName).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // input state
    var teamNameInput by mutableStateOf("")
        private set
    var sportInput by mutableStateOf("Cricket")
        private set

    // ui state
    var isLoading by mutableStateOf(false)
        private set
    var showCreateForm by mutableStateOf(false)
        private set
    var showLeaveDialog by mutableStateOf(false)
        private set

    fun onTeamNameChange(v: String) { teamNameInput = v }
    fun onSportChange(v: String) { sportInput = v }
    fun toggleCreateForm() { showCreateForm = !showCreateForm }
    fun showLeaveConfirmation() { showLeaveDialog = true }
    fun hideLeaveConfirmation() { showLeaveDialog = false }

    // load team when we know the teamId
    fun loadTeam(teamId: String) {
        if (teamId.isEmpty()) return
        viewModelScope.launch {
            repository.getTeam(teamId).collect { team ->
                _currentTeam.value = team
            }
        }
    }

    fun createTeam(onSuccess: () -> Unit) {
        if (teamNameInput.isBlank()) return
        isLoading = true
        viewModelScope.launch {
            repository.createTeam(
                teamName = teamNameInput,
                sport = sportInput,
                captainId = userId,
                captainName = displayName
            )
            isLoading = false
            showCreateForm = false
            onSuccess()
        }
    }

    fun joinTeam(team: Team, onSuccess: () -> Unit) {
        isLoading = true
        viewModelScope.launch {
            repository.joinTeam(
                teamId = team.id,
                userId = userId,
                displayName = displayName,
                teamName = team.teamName
            )
            isLoading = false
            onSuccess()
        }
    }

    fun leaveTeam(teamId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.leaveTeam(
                teamId = teamId,
                userId = userId,
                displayName = displayName
            )
            showLeaveDialog = false
            _currentTeam.value = null
            onSuccess()
        }
    }
}