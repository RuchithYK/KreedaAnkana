package com.example.kreedaankana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kreedaankana.ui.theme.*
import com.example.kreedaankana.ui.theme.screens.*
import com.example.kreedaankana.viewmodel.*
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KreedaAnkanaTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    
    // ViewModels
    val bookingViewModel: BookingViewModel = viewModel()
    val scoreViewModel: ScoreViewModel = viewModel()
    val challengeViewModel: ChallengeViewModel = viewModel()
    val teamViewModel: TeamViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()

    // Collect userProfile from ProfileViewModel
    val userProfile by profileViewModel.userProfile.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            // Success! Navigate to Home if we are currently on the login screen
            if (navController.currentDestination?.route == "login") {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
            profileViewModel.createProfileIfNotExists()
        } else {
            // Not logged in, ensure we are on the login screen
            if (navController.currentDestination?.route != "login") {
                navController.navigate("login") {
                    popUpTo(0)
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (isLoggedIn) {
                SportBottomNavigation(navController)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = navController,
                startDestination = if (isLoggedIn) "home" else "login"
            ) {
                composable("login") {
                    LoginScreen(
                        authViewModel = authViewModel,
                        webClientId = "341337941748-7pvd9am8nn4eathgs1tl3ah2cb1qtv26.apps.googleusercontent.com"
                    )
                }
                composable("home") {
                    HomeScreen(
                        homeViewModel = homeViewModel,
                        authViewModel = authViewModel,
                        navController = navController,
                        userName = userProfile?.displayName ?: ""
                    )
                }
                composable("calendar") {
                    CalendarScreen(
                        bookingViewModel = bookingViewModel,
                        authViewModel = authViewModel,
                        navController = navController
                    )
                }
                composable("book_slot") {
                    BookSlotScreen(
                        bookingViewModel = bookingViewModel,
                        authViewModel = authViewModel,
                        navController = navController
                    )
                }
                composable("challenges") {
                    ChallengeScreen(
                        challengeViewModel = challengeViewModel,
                        authViewModel = authViewModel,
                        userProfile = userProfile
                    )
                }
                composable("scores") {
                    ScoreScreen(
                        scoreViewModel = scoreViewModel,
                        authViewModel = authViewModel
                    )
                }
                composable("team") {
                    TeamScreen(
                        teamViewModel = teamViewModel,
                        userProfile = userProfile
                    )
                }
                composable("profile") {
                    ProfileScreen(
                        profileViewModel = profileViewModel,
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo(0)
                            }
                        }
                    )
                }
            }
        }
    }
}

data class NavItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun SportBottomNavigation(navController: NavHostController) {
    val items = listOf(
        NavItem("home", "HOME", Icons.Default.Home),
        NavItem("calendar", "ARENA", Icons.Default.SportsScore),
        NavItem("challenges", "VERSUS", Icons.Default.FlashOn),
        NavItem("scores", "WALL", Icons.Default.EmojiEvents),
        NavItem("team", "TEAM", Icons.Default.Groups)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Floating glassy bottom bar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp)
            .background(
                Brush.verticalGradient(listOf(SportSurface2.copy(alpha = 0.95f), SportBlack)),
                RoundedCornerShape(20.dp)
            )
            .border(1.dp, SportBorderLight.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                val color = if (selected) SportOrange else SportGreyLight

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = color,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                        color = color,
                        letterSpacing = 1.sp
                    )
                    
                    if (selected) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height(2.dp)
                                .background(SportOrange, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }
    }
}