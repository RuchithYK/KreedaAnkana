package com.example.kreedaankana.ui.theme.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.kreedaankana.ui.theme.*
import com.example.kreedaankana.viewmodel.AuthViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

enum class AuthMode { LOGIN, SIGNUP, FORGOT_PASSWORD }

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    webClientId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading = authViewModel.isLoading
    val errorMessage = authViewModel.errorMessage
    val forgotPasswordSent = authViewModel.forgotPasswordSent

    LaunchedEffect(authMode) { authViewModel.clearError() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SportBlack, Color(0xFF0D0D20), SportBlack)
                )
            )
    ) {
        // Decorative gradient strip at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(SportOrange, SportGradMid, SportBlue)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // ── BRAND HEADER ──
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🏟️", fontSize = 36.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "KREEDA",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = SportWhite,
                        letterSpacing = 2.sp,
                        lineHeight = 44.sp
                    )
                    Text(
                        text = "ANKANA",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = SportOrange,
                        letterSpacing = 2.sp,
                        lineHeight = 44.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Sport tags row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("🏏", "⚽", "🏀", "🎾").forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .border(1.dp, SportBorder, RoundedCornerShape(4.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = emoji, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── MODE TITLE ──
            Text(
                text = when (authMode) {
                    AuthMode.LOGIN -> "WELCOME BACK"
                    AuthMode.SIGNUP -> "JOIN THE ARENA"
                    AuthMode.FORGOT_PASSWORD -> "RESET ACCESS"
                },
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = SportWhite,
                letterSpacing = 2.sp
            )
            Text(
                text = when (authMode) {
                    AuthMode.LOGIN -> "Sign in to your sports hub"
                    AuthMode.SIGNUP -> "Create your player profile"
                    AuthMode.FORGOT_PASSWORD -> "We'll email you a reset link"
                },
                fontSize = 14.sp,
                color = SportGreyLight,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── SUCCESS / ERROR ALERTS ──
            if (forgotPasswordSent) {
                SportAlertBox(
                    message = "✓ Reset email dispatched! Check your inbox.",
                    color = SportGreen,
                    bgColor = Color(0xFF001A0A)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (errorMessage.isNotEmpty()) {
                SportAlertBox(
                    message = "⚠ ${errorMessage}",
                    color = SportRed,
                    bgColor = SportRedBg
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── GOOGLE SIGN IN ──
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val credentialManager = CredentialManager.create(context)
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setServerClientId(webClientId)
                                .setFilterByAuthorizedAccounts(false)
                                .build()
                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()
                            val result = credentialManager.getCredential(
                                request = request,
                                context = context as Activity
                            )
                            val googleIdToken = GoogleIdTokenCredential
                                .createFrom(result.credential.data)
                                .idToken
                            authViewModel.handleGoogleSignIn(googleIdToken)
                        } catch (e: GetCredentialException) {
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SportSurface2,
                    contentColor = SportWhite
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(
                        listOf(SportBorderLight, SportBorderLight)
                    )
                ),
                enabled = !isLoading
            ) {
                Text(
                    text = "G   CONTINUE WITH GOOGLE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── DIVIDER ──
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(SportBorderLight))
                Text(
                    text = "  OR  ",
                    color = SportGreyLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Box(modifier = Modifier.weight(1f).height(1.dp).background(SportBorderLight))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── DISPLAY NAME (signup only) ──
            if (authMode == AuthMode.SIGNUP) {
                SportFieldLabel("PLAYER NAME")
                Spacer(modifier = Modifier.height(6.dp))
                SportTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    placeholder = "Enter your name"
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // ── EMAIL ──
            SportFieldLabel("EMAIL")
            Spacer(modifier = Modifier.height(6.dp))
            SportTextField(
                value = email,
                onValueChange = { email = it; authViewModel.clearError() },
                placeholder = "your@email.com",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── PASSWORD ──
            if (authMode != AuthMode.FORGOT_PASSWORD) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SportFieldLabel("PASSWORD")
                    if (authMode == AuthMode.LOGIN) {
                        TextButton(
                            onClick = { authMode = AuthMode.FORGOT_PASSWORD },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Forgot?",
                                fontSize = 12.sp,
                                color = SportOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                SportPasswordField(
                    value = password,
                    onValueChange = { password = it; authViewModel.clearError() },
                    visible = passwordVisible,
                    onToggleVisibility = { passwordVisible = !passwordVisible }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // ── CONFIRM PASSWORD (signup) ──
            if (authMode == AuthMode.SIGNUP) {
                SportFieldLabel("CONFIRM PASSWORD")
                Spacer(modifier = Modifier.height(6.dp))
                SportPasswordField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    visible = false,
                    onToggleVisibility = {}
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── MAIN CTA ──
            Button(
                onClick = {
                    when (authMode) {
                        AuthMode.LOGIN -> authViewModel.signInWithEmail(email, password)
                        AuthMode.SIGNUP -> {
                            if (password != confirmPassword) return@Button
                            authViewModel.signUpWithEmail(email, password, displayName)
                        }
                        AuthMode.FORGOT_PASSWORD -> authViewModel.sendPasswordResetEmail(email)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SportOrange,
                    contentColor = SportWhite
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = SportWhite,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = when (authMode) {
                            AuthMode.LOGIN -> "SIGN IN  →"
                            AuthMode.SIGNUP -> "CREATE ACCOUNT  →"
                            AuthMode.FORGOT_PASSWORD -> "SEND RESET LINK  →"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── SWITCH MODE ──
            TextButton(
                onClick = {
                    authMode = when (authMode) {
                        AuthMode.LOGIN -> AuthMode.SIGNUP
                        else -> AuthMode.LOGIN
                    }
                    authViewModel.clearError()
                    authViewModel.clearForgotPasswordSent()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when (authMode) {
                        AuthMode.LOGIN -> "New player? Create an account"
                        AuthMode.SIGNUP -> "Already registered? Sign in"
                        AuthMode.FORGOT_PASSWORD -> "Remember your password? Sign in"
                    },
                    fontSize = 13.sp,
                    color = SportGreyLight,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── SHARED COMPONENTS ──

@Composable
private fun SportAlertBox(message: String, color: Color, bgColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(14.dp)
    ) {
        Text(text = message, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SportFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        color = SportGreyLight,
        letterSpacing = 1.sp
    )
}

@Composable
private fun SportTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(text = placeholder, color = SportGreyMuted, fontSize = 14.sp)
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = SportWhite,
            unfocusedTextColor = SportWhite,
            cursorColor = SportOrange,
            focusedBorderColor = SportOrange,
            unfocusedBorderColor = SportBorderLight
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}

@Composable
private fun SportPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onToggleVisibility: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = SportGreyLight
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = SportWhite,
            unfocusedTextColor = SportWhite,
            cursorColor = SportOrange,
            focusedBorderColor = SportOrange,
            unfocusedBorderColor = SportBorderLight
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}
