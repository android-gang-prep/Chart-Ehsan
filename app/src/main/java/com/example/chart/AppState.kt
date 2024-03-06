package com.example.chart

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope

class AppState(
    val context: Context,
    val navController: NavHostController,
    val scope:CoroutineScope,
) {
}

@Composable
fun rememberAppState():AppState {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    return remember {
        AppState(navController = navController, context = context, scope = scope)
    }
}

val LocalAppState = staticCompositionLocalOf<AppState> { error("No State Provided Yet!") }