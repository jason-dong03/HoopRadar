package com.main.hoopradar

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.main.hoopradar.navigation.NavGraph

@Composable
fun HoopRadarApp() {
    val navController = rememberNavController()
    NavGraph(navController = navController)
}