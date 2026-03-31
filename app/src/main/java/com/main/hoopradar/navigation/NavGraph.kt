package com.main.hoopradar.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.main.hoopradar.ui.screen.home.HomeScreen
import com.main.hoopradar.ui.screen.login.LoginScreen
import com.main.hoopradar.ui.screen.map.NearbyCourtsScreen
import com.main.hoopradar.ui.screen.profile.ProfileScreen
import com.main.hoopradar.ui.screen.run.CreateRunScreen
import com.main.hoopradar.ui.screen.run.RunDetailsScreen
import com.main.hoopradar.ui.screen.court.CourtDetailsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                onNearbyCourtsClick = { navController.navigate(Routes.NEARBY_COURTS) },
                onCreateRunClick = { navController.navigate(Routes.CREATE_RUN) },
                onProfileClick = { navController.navigate(Routes.PROFILE) }
            )
        }
        composable(Routes.NEARBY_COURTS) {
            NearbyCourtsScreen(
                onBack = { navController.popBackStack() },
                onCourtClick = { navController.navigate(Routes.COURT_DETAILS) },
                onRunClick = { navController.navigate(Routes.RUN_DETAILS) }
            )
        }
        composable(Routes.CREATE_RUN) {
            CreateRunScreen(onRunCreated = { navController.popBackStack() }, onBack = { navController.popBackStack()})
        }
        composable(Routes.RUN_DETAILS) {
            RunDetailsScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.COURT_DETAILS) {
            CourtDetailsScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.PROFILE) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }
    }
}