package com.example.kreedaankana.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kreedaankana.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val repository = FirebaseRepository()
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _isLoggedIn = MutableStateFlow(firebaseAuth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        firebaseAuth.addAuthStateListener { auth ->
            val user = auth.currentUser
            _isLoggedIn.value = user != null
            if (user != null) {
                _userName.value = user.displayName ?: user.email ?: ""
                _userEmail.value = user.email ?: ""
            } else {
                _userName.value = ""
                _userEmail.value = ""
            }
        }
    }

    private val _userName = MutableStateFlow(firebaseAuth.currentUser?.displayName ?: "")
    val userName: StateFlow<String> = _userName

    private val _userEmail = MutableStateFlow(firebaseAuth.currentUser?.email ?: "")
    val userEmail: StateFlow<String> = _userEmail

    val userId get() = repository.getCurrentUser()?.uid ?: ""

    // UI state for auth screen
    var errorMessage by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var forgotPasswordSent by mutableStateOf(false)
        private set

    fun clearError() { errorMessage = "" }
    fun clearForgotPasswordSent() { forgotPasswordSent = false }

    // Google Sign In
    fun handleGoogleSignIn(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        isLoading = true
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                viewModelScope.launch {
                    try {
                        result.user?.reload()?.await()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val user = firebaseAuth.currentUser
                    if (user?.uid?.isNotEmpty() == true) {
                        _userName.value = user.displayName ?: user.email ?: ""
                        _userEmail.value = user.email ?: ""
                        _isLoggedIn.value = true
                    }
                    isLoading = false
                }
            }
            .addOnFailureListener { e ->
                errorMessage = e.message ?: "Google sign in failed"
                isLoading = false
            }
    }

    // Email Sign Up
    fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String
    ) {
        if (email.isBlank() || password.isBlank() || displayName.isBlank()) {
            errorMessage = "Please fill all fields!"
            return
        }
        if (password.length < 6) {
            errorMessage = "Password must be at least 6 characters!"
            return
        }
        isLoading = true
        errorMessage = ""
        viewModelScope.launch {
            try {
                // create account
                val result = firebaseAuth
                    .createUserWithEmailAndPassword(email, password)
                    .await()

                // update display name
                val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                    this.displayName = displayName
                }
                result.user?.updateProfile(profileUpdates)?.await()
                result.user?.reload()?.await()

                val user = firebaseAuth.currentUser
                if (user?.uid?.isNotEmpty() == true) {
                    _userName.value = displayName
                    _userEmail.value = email
                    _isLoggedIn.value = true
                }
            } catch (e: Exception) {
                errorMessage = when {
                    e.message?.contains("email address is already") == true ->
                        "This email is already registered!"
                    e.message?.contains("badly formatted") == true ->
                        "Invalid email format!"
                    else -> e.message ?: "Sign up failed"
                }
            } finally {
                isLoading = false
            }
        }
    }

    // Email Sign In
    fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please enter email and password!"
            return
        }
        isLoading = true
        errorMessage = ""
        viewModelScope.launch {
            try {
                val result = firebaseAuth
                    .signInWithEmailAndPassword(email, password)
                    .await()
                result.user?.reload()?.await()
                val user = firebaseAuth.currentUser
                if (user?.uid?.isNotEmpty() == true) {
                    _userName.value = user.displayName ?: user.email ?: ""
                    _userEmail.value = user.email ?: ""
                    _isLoggedIn.value = true
                }
            } catch (e: Exception) {
                errorMessage = when {
                    e.message?.contains("no user record") == true ->
                        "No account found with this email!"
                    e.message?.contains("password is invalid") == true ->
                        "Wrong password!"
                    e.message?.contains("badly formatted") == true ->
                        "Invalid email format!"
                    else -> e.message ?: "Sign in failed"
                }
            } finally {
                isLoading = false
            }
        }
    }

    // Forgot Password
    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            errorMessage = "Please enter your email!"
            return
        }
        isLoading = true
        errorMessage = ""
        viewModelScope.launch {
            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                forgotPasswordSent = true
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to send reset email"
            } finally {
                isLoading = false
            }
        }
    }

    fun signOut() {
        repository.signOut()
        _isLoggedIn.value = false
        _userName.value = ""
        _userEmail.value = ""
    }
}