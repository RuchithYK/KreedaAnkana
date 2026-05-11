package com.example.kreedaankana.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kreedaankana.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.ExperimentalCoroutinesApi

class HomeViewModel : ViewModel() {

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

    // Use Eagerly so stats are available immediately when screen opens
    @OptIn(ExperimentalCoroutinesApi::class)
    val recentBookings = authState.flatMapLatest { user ->
        if (user != null) repository.getRecentBookings() else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val recentChallenges = authState.flatMapLatest { user ->
        if (user != null) repository.getRecentChallenges() else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )
}