package com.example.chart.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
        Button(onClick = { appState.navController.navigate(Routes.PAI.route) }, shape = RoundedCornerShape(8.dp)) {
            Text(text = "PAI")
        }
        Text(text = "Candle Stick")
        Row (horizontalArrangement = Arrangement.spacedBy(8.dp)){
            Button(onClick = { appState.navController.navigate(Routes.CandleStick.route+"/DOGE") }, shape = RoundedCornerShape(8.dp)) {
                Text(text = "Doge Stick 4h")
            }
            Button(onClick = { appState.navController.navigate(Routes.CandleStick.route+"/BTC") }, shape = RoundedCornerShape(8.dp)) {
                Text(text = "BTC Stick 4h")
            }
            Button(onClick = { appState.navController.navigate(Routes.CandleStick.route+"/TRX") }, shape = RoundedCornerShape(8.dp)) {
                Text(text = "TRX Stick 1h")
            }
        }
    }
}