package com.example.chart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class AppState(
    val navController: NavHostController
) {
}

@Composable
fun rememberAppState():AppState {
    val navController = rememberNavController()

    return remember {
        AppState(navController = navController)
    }
}

val LocalAppState = staticCompositionLocalOf<AppState> { error("No State Provided Yet!") }