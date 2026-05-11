package com.example.kreedaankana.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kreedaankana.data.UserProfile
import com.example.kreedaankana.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ProfileViewModel : ViewModel() {

    private val repository = FirebaseRepository()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val currentUser get() = firebaseAuth.currentUser
    val userId get() = currentUser?.uid ?: ""
    val displayName get() = currentUser?.displayName ?: ""
    val email get() = currentUser?.email ?: ""
    val photoUrl get() = currentUser?.photoUrl?.toString() ?: ""

    @OptIn(ExperimentalCoroutinesApi::class)
    val userProfile = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }.flatMapLatest { user ->
        if (user != null) {
            repository.getUserProfile(user.uid)
        } else {
            flowOf(null)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    var teamNameInput by mutableStateOf("")
        private set
    var isEditing by mutableStateOf(false)
        private set
    var isSaving by mutableStateOf(false)
        private set
    var showDeleteDialog by mutableStateOf(false)
        private set
    var isDeleting by mutableStateOf(false)
        private set
    var deleteError by mutableStateOf("")
        private set

    fun onTeamNameChange(value: String) { teamNameInput = value }

    fun startEditing(currentTeamName: String) {
        teamNameInput = currentTeamName
        isEditing = true
    }

    fun cancelEditing() { isEditing = false }

    fun saveTeamName() {
        if (userId.isEmpty()) return
        isSaving = true
        viewModelScope.launch {
            try {
                repository.updateTeamName(userId, teamNameInput)
                isEditing = false
            } finally {
                isSaving = false
            }
        }
    }

    fun createProfileIfNotExists() {
        if (userId.isEmpty()) return
        viewModelScope.launch {
            try {
                // ✅ FIX: retry a few times waiting for Firestore to respond
                var attempts = 0
                var profile = userProfile.value
                while (profile == null && attempts < 5) {
                    delay(300)
                    profile = userProfile.value
                    attempts++
                }
                if (profile == null) {
                    repository.saveUserProfile(
                        UserProfile(
                            userId = userId,
                            displayName = displayName,
                            email = email,
                            photoUrl = photoUrl,
                            teamName = "",
                            teamId = ""
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showDeleteConfirmation() { showDeleteDialog = true }
    fun hideDeleteConfirmation() { showDeleteDialog = false }

    fun deleteAccount(onDeleted: () -> Unit) {
        if (userId.isEmpty()) return
        isDeleting = true
        deleteError = ""

        viewModelScope.launch {
            try {
                // Step 1 — delete all Firestore data
                repository.deleteAllUserData(userId)

                // Step 2 — try to delete Firebase Auth account
                try {
                    firebaseAuth.currentUser?.delete()?.await()
                } catch (authDeleteError: Exception) {
                    authDeleteError.printStackTrace()
                    deleteError = "Session expired. Please log out, sign in again, then delete."
                    isDeleting = false
                    return@launch
                }

                // Step 3 - sign out completely
                firebaseAuth.signOut()

                // Step 4 — navigate away
                onDeleted()

            } catch (e: Exception) {
                e.printStackTrace()
                deleteError = "Failed to delete: ${e.message}"
                isDeleting = false
            }
        }
    }

    fun signOut() { repository.signOut() }
}