package com.example.kreedaankana.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kreedaankana.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    // is user logged in or not
    private val _isLoggedIn = MutableStateFlow(
        repository.getCurrentUser() != null
    )
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // current user's display name
    val userName = repository.getCurrentUser()?.displayName ?: ""
    val userEmail = repository.getCurrentUser()?.email ?: ""
    val userId = repository.getCurrentUser()?.uid ?: ""

    // called after Google Sign In is done on UI side
    // idToken comes from Google Sign In result
    fun handleGoogleSignIn(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnSuccessListener {
                _isLoggedIn.value = true
            }
    }

    fun signOut() {
        repository.signOut()
        _isLoggedIn.value = false
    }
}