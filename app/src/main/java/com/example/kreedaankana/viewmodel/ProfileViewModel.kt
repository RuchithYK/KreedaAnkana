package com.example.kreedaankana.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kreedaankana.data.UserProfile
import com.example.kreedaankana.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = FirebaseRepository()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    val userId = currentUser?.uid ?: ""
    val displayName = currentUser?.displayName ?: ""
    val email = currentUser?.email ?: ""
    val photoUrl = currentUser?.photoUrl?.toString() ?: ""

    // live user profile from firestore
    val userProfile = repository.getUserProfile(userId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // editable team name input
    var teamNameInput by mutableStateOf("")
        private set

    var isEditing by mutableStateOf(false)
        private set

    // show delete confirmation dialog
    var showDeleteDialog by mutableStateOf(false)
        private set

    fun onTeamNameChange(value: String) { teamNameInput = value }

    fun startEditing(currentTeamName: String) {
        teamNameInput = currentTeamName
        isEditing = true
    }

    fun cancelEditing() { isEditing = false }

    fun saveTeamName() {
        viewModelScope.launch {
            repository.updateTeamName(userId, teamNameInput)
            isEditing = false
        }
    }

    // create profile when user first logs in
    fun createProfileIfNotExists() {
        viewModelScope.launch {
            val existing = repository.getUserProfile(userId)
            // only create if profile doesn't exist
            if (userProfile.value == null) {
                repository.saveUserProfile(
                    UserProfile(
                        userId = userId,
                        displayName = displayName,
                        email = email,
                        photoUrl = photoUrl,
                        teamName = ""
                    )
                )
            }
        }
    }

    fun showDeleteConfirmation() { showDeleteDialog = true }
    fun hideDeleteConfirmation() { showDeleteDialog = false }

    fun deleteAccount(onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteAllUserData(userId)
            onDeleted()
        }
    }

    fun signOut() {
        repository.signOut()
    }
}