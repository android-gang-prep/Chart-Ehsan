package com.example.chart

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class AppState(
    val context: Context,
    val navController: NavHostController,
) {
}

@Composable
fun rememberAppState():AppState {
    val navController = rememberNavController()
    val context = LocalContext.current
    return remember {
        AppState(navController = navController, context = context)
    }
}

val LocalAppState = staticCompositionLocalOf<AppState> { error("No State Provided Yet!") }