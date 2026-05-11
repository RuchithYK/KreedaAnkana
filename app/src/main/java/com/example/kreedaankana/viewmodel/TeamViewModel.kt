package com.example.kreedaankana.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kreedaankana.data.Team
import com.example.kreedaankana.data.TeamMember
import com.example.kreedaankana.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TeamViewModel : ViewModel() {

    private val repository = FirebaseRepository()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    val userId = currentUser?.uid ?: ""
    val displayName = currentUser?.displayName ?: ""

    val allTeams = repository.getAllTeams().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val _currentTeam = MutableStateFlow<Team?>(null)
    val currentTeam: StateFlow<Team?> = _currentTeam

    val teamScores = repository.getTeamScores(displayName).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    var teamNameInput by mutableStateOf("")
        private set
    var sportInput by mutableStateOf("Cricket")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf("")
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
    fun clearError() { errorMessage = "" }

    private var teamListenerJob: Job? = null
    private var lastLoadedTeamId: String = ""

    fun loadTeam(teamId: String) {
        if (teamId.isEmpty() || teamId == lastLoadedTeamId) return
        lastLoadedTeamId = teamId

        teamListenerJob?.cancel()
        teamListenerJob = viewModelScope.launch {
            repository.getTeam(teamId).collect { team ->
                _currentTeam.value = team
            }
        }
    }

    fun createTeam(onSuccess: () -> Unit) {
        if (teamNameInput.isBlank()) {
            errorMessage = "Please enter a team name"
            return
        }
        isLoading = true
        errorMessage = ""
        viewModelScope.launch {
            try {
                val newTeamId = repository.createTeam(
                    teamName = teamNameInput,
                    sport = sportInput,
                    captainId = userId,
                    captainName = displayName
                )
                if (newTeamId.isNotEmpty()) {
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    _currentTeam.value = Team(
                        id = newTeamId,
                        teamName = teamNameInput,
                        sport = sportInput,
                        captainId = userId,
                        captainName = displayName,
                        members = listOf(
                            TeamMember(userId = userId, displayName = displayName, joinedAt = today)
                        ),
                        createdAt = today
                    )
                    lastLoadedTeamId = ""
                    loadTeam(newTeamId)
                    teamNameInput = ""
                    showCreateForm = false
                    onSuccess()
                } else {
                    errorMessage = "Failed to create team"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "An error occurred"
            } finally {
                isLoading = false
            }
        }
    }

    fun joinTeam(team: Team, onSuccess: () -> Unit) {
        if (isLoading) return
        isLoading = true
        errorMessage = ""
        viewModelScope.launch {
            try {
                repository.joinTeam(
                    teamId = team.id,
                    userId = userId,
                    displayName = displayName,
                    teamName = team.teamName
                )
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val updatedTeam = team.copy(
                    members = team.members + TeamMember(
                        userId = userId,
                        displayName = displayName,
                        joinedAt = today
                    )
                )
                _currentTeam.value = updatedTeam
                lastLoadedTeamId = ""
                loadTeam(team.id)
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to join team"
            } finally {
                isLoading = false
            }
        }
    }

    fun leaveTeam(teamId: String, onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = ""
        viewModelScope.launch {
            try {
                repository.leaveTeam(
                    teamId = teamId,
                    userId = userId,
                    displayName = displayName
                )
                _currentTeam.value = null
                lastLoadedTeamId = ""
                teamListenerJob?.cancel()
                teamListenerJob = null
                showLeaveDialog = false
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to leave team"
            } finally {
                isLoading = false
            }
        }
    }
}