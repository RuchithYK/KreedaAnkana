package com.example.kreedaankana


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kreedaankana.ui.theme.screens.BookSlotScreen
import com.example.kreedaankana.ui.theme.screens.CalendarScreen
import com.example.kreedaankana.ui.theme.screens.ChallengeScreen
import com.example.kreedaankana.ui.theme.screens.HomeScreen
import com.example.kreedaankana.ui.theme.screens.LoginScreen
import com.example.kreedaankana.ui.theme.screens.ProfileScreen
import com.example.kreedaankana.ui.theme.screens.ScoreScreen
import com.example.kreedaankana.ui.theme.screens.TeamScreen
import com.example.kreedaankana.viewmodel.AuthViewModel
import com.example.kreedaankana.viewmodel.BookingViewModel
import com.example.kreedaankana.viewmodel.ChallengeViewModel
import com.example.kreedaankana.viewmodel.HomeViewModel
import com.example.kreedaankana.viewmodel.ProfileViewModel
import com.example.kreedaankana.viewmodel.ScoreViewModel
import com.example.kreedaankana.viewmodel.TeamViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KreedaAnkanaApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KreedaAnkanaApp() {
    val navController = rememberNavController()

    // one instance of each viewmodel shared across screens
    val authViewModel: AuthViewModel = viewModel()
    val bookingViewModel: BookingViewModel = viewModel()
    val challengeViewModel: ChallengeViewModel = viewModel()
    val scoreViewModel: ScoreViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()

    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val userProfile by profileViewModel.userProfile.collectAsState()

    val webClientId = "341337941748-7pvd9am8nn4eathgs1tl3ah2cb1qtv26.apps.googleusercontent.com"

    // if not logged in → show login screen
    // if logged in → show main app
    if (!isLoggedIn) {
        LoginScreen(
            authViewModel = authViewModel,
            webClientId = webClientId
        )
    } else {
        LaunchedEffect(Unit) {
            profileViewModel.createProfileIfNotExists()
        }

        // bottom navigation items
        val bottomNavItems = listOf(
            Triple("home", "Home", Icons.Default.Home),
            Triple("calendar", "Calendar", Icons.Default.DateRange),
            Triple("challenges", "Challenges", Icons.Default.ThumbUp),
            Triple("scores", "Scores", Icons.Default.Star),
            Triple("team", "Team", Icons.Default.Group)
        )

        val currentRoute = navController
            .currentBackStackEntryAsState().value?.destination?.route

        val showBottomBar = currentRoute in listOf(
            "home", "calendar", "challenges", "scores","team"
        )
        val teamViewModel: TeamViewModel = viewModel()

        Scaffold(
            bottomBar = {
                // only show bottom bar on main screens, not on BookSlot screen
                if (showBottomBar) {
                    NavigationBar {
                        bottomNavItems.forEach { (route, label, icon) ->
                            NavigationBarItem(
                                selected = currentRoute == route,
                                onClick = {
                                    navController.navigate(route) {
                                        // pop back to start so back button works correctly
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(icon, contentDescription = label) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            },

        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("home") {
                    HomeScreen(
                        homeViewModel = homeViewModel,
                        authViewModel = authViewModel,
                        navController = navController
                    )
                }
                composable("calendar") {
                    CalendarScreen(
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
                composable("book_slot") {
                    BookSlotScreen(
                        bookingViewModel = bookingViewModel,
                        authViewModel = authViewModel,
                        navController = navController
                    )
                }
                composable("profile") {
                    ProfileScreen(
                        profileViewModel = profileViewModel,
                        onLogout = {
                            authViewModel.signOut()
                        },
                        onAccountDeleted = {
                            authViewModel.signOut()
                        }
                    )
                }
                composable("team") {
                    val userProfile by profileViewModel.userProfile.collectAsState()
                    TeamScreen(
                        teamViewModel = teamViewModel,
                        userProfile = userProfile,
                        onTeamUpdated = {}
                    )
                }
            }
        }
    }
}