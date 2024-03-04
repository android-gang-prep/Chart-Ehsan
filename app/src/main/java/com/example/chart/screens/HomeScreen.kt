package com.example.chart.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chart.LocalAppState
import com.example.chart.Routes

@Composable
fun HomeScreen() {

    val appState = LocalAppState.current

    Column(modifier=Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Button(onClick = { appState.navController.navigate(Routes.SleepDuration.route) }, shape = RoundedCornerShape(8.dp)) {
            Text(text = "Sleep Duration")
        }
        Button(onClick = { appState.navController.navigate(Routes.NightSleep.route) }, shape = RoundedCornerShape(8.dp)) {
            Text(text = "Night Sleep")
        }
        Button(onClick = { appState.navController.navigate(Routes.HeartRate.route) }, shape = RoundedCornerShape(8.dp)) {
            Text(text = "Heart Rate")
        }
    }
}