package com.example.chart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.chart.screens.CandleStickScreen
import com.example.chart.screens.PAIScreen
import com.example.chart.screens.HeartrateScreen
import com.example.chart.screens.HomeScreen
import com.example.chart.screens.NightSleepScreen
import com.example.chart.screens.SleepDurationScreen
import com.example.chart.ui.theme.ChartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChartTheme(false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val appState = LocalAppState.current

                    NavHost(
                        navController = appState.navController,
                        startDestination = Routes.Home.route,
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            )
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                tween(500)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                tween(500)
                            )
                        }
                    ) {
                        composable(Routes.Home.route) {
                            HomeScreen()
                        }
                        composable(Routes.SleepDuration.route) {
                            SleepDurationScreen()
                        }
                        composable(Routes.HeartRate.route) {
                            HeartrateScreen()
                        }
                        composable(Routes.NightSleep.route) {
                            NightSleepScreen()
                        }
                        composable(Routes.PAI.route) {
                            PAIScreen()
                        }
                        composable(Routes.CandleStick.route+"/{crypto}", arguments = listOf(
                            navArgument("crypto") { type = NavType.StringType }
                        )) {
                            CandleStickScreen(crypto = it.arguments?.getString("crypto") ?: "BTC")
                        }
                    }
                }
            }
        }
    }
}
