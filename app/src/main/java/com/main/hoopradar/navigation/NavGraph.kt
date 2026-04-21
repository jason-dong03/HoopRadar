package com.main.hoopradar.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.main.hoopradar.ui.screen.court.CourtDetailsScreen
import com.main.hoopradar.ui.screen.home.HomeScreen
import com.main.hoopradar.ui.screen.login.LoginScreen
import com.main.hoopradar.ui.screen.map.NearbyCourtsScreen
import com.main.hoopradar.ui.screen.profile.ProfileScreen
import com.main.hoopradar.ui.screen.run.CreateRunScreen
import com.main.hoopradar.ui.screen.run.RunChatScreen
import com.main.hoopradar.ui.screen.run.RunDetailsScreen
import com.main.hoopradar.viewmodel.RunsViewModel

@Composable
fun NavGraph(navController: NavHostController) { // defines all app navigation routes and screens
    val runsViewModel: RunsViewModel = viewModel() // shared viewmodel for run related screens

    NavHost(navController = navController, startDestination = Routes.LOGIN) { // navhost manages navigation between app screens
        composable(Routes.LOGIN) { // login screen route
            LoginScreen(
                onLoginSuccess = { // navigate to home after successful login
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.HOME) { // home screen route
            HomeScreen(
                onNearbyCourtsClick = { navController.navigate(Routes.NEARBY_COURTS) },
                onCreateRunClick = { navController.navigate(Routes.CREATE_RUN) },
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onRunChatClick = { runId, courtName ->
                    navController.navigate(Routes.runChat(runId, courtName))
                },
                runsViewModel = runsViewModel
            )
        }
        composable(Routes.NEARBY_COURTS) { // nearby courts screen route
            NearbyCourtsScreen(
                onBack = { navController.popBackStack() },
                onCourtClick = { navController.navigate(Routes.COURT_DETAILS) },
                onRunClick = { runId -> navController.navigate(Routes.runDetails(runId)) },
                runsViewModel = runsViewModel
            )
        }
        composable(Routes.CREATE_RUN) { // create run screen route
            CreateRunScreen(
                onRunCreated = { navController.popBackStack() }, // return to previous screen after creating a run
                onBack = { navController.popBackStack() },
                runsViewModel = runsViewModel
            )
        }
        composable( // create details screen route with runid argument
            route = Routes.RUN_DETAILS,
            arguments = listOf(navArgument("runId") { type = NavType.StringType })
        ) { backStackEntry ->
            val runId = backStackEntry.arguments?.getString("runId") ?: ""
            val runs by runsViewModel.runs.collectAsState()
            val run = runs.find { it.id == runId }
            if (run != null) { // only show screen if matching run exists
                RunDetailsScreen(
                    run = run,
                    onBack = { navController.popBackStack() },
                    runsViewModel = runsViewModel
                )
            }
        }
        composable( // run chat screen route with runid and courtname arguments
            route = Routes.RUN_CHAT,
            arguments = listOf(
                navArgument("runId") { type = NavType.StringType },
                navArgument("courtName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val runId = backStackEntry.arguments?.getString("runId") ?: "" // read runid argument
            val courtName = java.net.URLDecoder.decode( // decode courtname because it may be url-encoded in navigation route
                backStackEntry.arguments?.getString("courtName") ?: "", "UTF-8"
            )
            RunChatScreen(
                runId = runId,
                courtName = courtName,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.COURT_DETAILS) { // court details screen route
            CourtDetailsScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.PROFILE) { // profile screen route
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onSignOut = { // navigate back to login after sign out
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
