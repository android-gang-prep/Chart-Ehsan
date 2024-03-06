package com.example.chart.screens

import android.content.pm.ActivityInfo
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chart.LocalAppState
import com.example.chart.MainActivity
import com.example.chart.data.getChartData
import com.example.chart.data.getHeartRateData
import com.example.chart.data.getHeartRateTime
import com.example.chart.model.HeartRate
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun HeartrateScreen() {

    val appState = LocalAppState.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        (appState.navController.context as MainActivity)
            .requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            (appState.navController.context as MainActivity)
                .requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    val showChart = remember {
        mutableStateOf(false)
    }
    val chartProgress = remember {
        Animatable(0f)
    }

    val positionBallPercentage = remember {
        mutableStateOf(0)
    }

    val data = remember {
        mutableStateOf(getHeartRateData())
    }
    val chartData = remember {
        mutableStateOf(
            getChartData(30) + MutableList(3) { 700f }
        )
    }

    val heartTime = getHeartRateTime()
    val startTime = heartTime.first
    val endTime = heartTime.second

    val badgeX = remember {
        mutableFloatStateOf(0f)
    }
    val badgeXInRoot = remember {
        mutableFloatStateOf(0f)
    }

    val barColorAlpha = animateFloatAsState(
        targetValue = if (showChart.value) .4f else 1f,
        animationSpec = tween(500)
    )

    LaunchedEffect(showChart.value) {
        if (showChart.value) {
            chartProgress.animateTo(1f, tween(3000))
        } else {
            chartProgress.animateTo(0f, tween(3000))
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(modifier = Modifier.padding(start = 16.dp), onClick = {
            showChart.value = !showChart.value
        }, shape = RoundedCornerShape(8.dp)) {
            Text(text = "Show/Hide Chart")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.25f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            data.value.forEach {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width((it.value * 20).dp)
                                        .background(
                                            if (it.type == HeartRate.Type.Awake) it.type.color.copy(
                                                alpha = barColorAlpha.value
                                            ) else Color.Transparent
                                        )
                                )
                            }
                        }
                        Divider()
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.25f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            data.value.forEach {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width((it.value * 20).dp)
                                        .background(
                                            if (it.type == HeartRate.Type.Rem) it.type.color.copy(
                                                alpha = barColorAlpha.value
                                            ) else Color.Transparent
                                        )
                                )
                            }
                        }
                        Divider()
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.25f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            data.value.forEach {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width((it.value * 20).dp)
                                        .background(
                                            if (it.type == HeartRate.Type.Light) it.type.color.copy(
                                                alpha = barColorAlpha.value
                                            ) else Color.Transparent
                                        )
                                )
                            }
                        }
                        Divider()
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.25f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            data.value.forEach {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width((it.value * 20).dp)
                                        .background(
                                            if (it.type == HeartRate.Type.Damp) it.type.color.copy(
                                                alpha = barColorAlpha.value
                                            ) else Color.Transparent
                                        )
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(Color.DarkGray)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Text(text = "Awake", fontSize = 13.sp)
                    Text(text = "REM", fontSize = 13.sp)
                    Text(text = "Light", fontSize = 13.sp)
                    Text(text = "Damp", fontSize = 13.sp)
                }
            }
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 58.dp)
            ) {
                drawChart(
                    values = chartData.value,
                    height = size.height,
                    width = size.width,
                    color1 = Color(0xFFE4563D),
                    color2 = null,
                    color3 = null,
                    progress = chartProgress.value,
                    gradientProgress = 0f,
                    gradient = false,
                    ballInEndLine = true,
                    ballInLinePositionPercent = positionBallPercentage.value
                )
            }
            Box(modifier = Modifier
                .width(1.dp)
                .offset {
                    IntOffset(badgeXInRoot.value.toInt() + 30, 0)
                }
                .fillMaxHeight()
                .background(Color.Gray))
        }
        Row(modifier=Modifier.fillMaxWidth().padding(start = 16.dp, end = 32.dp,top = 12.dp,bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            val formatHeartTime = SimpleDateFormat("MM/dd HH:mm")
            Text(text = formatHeartTime.format(Date(startTime)))
            Text(text = formatHeartTime.format(Date(endTime)))
        }
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp)

                .background(Color.Gray)
                .padding(end = 58.dp), contentAlignment = Alignment.TopEnd
        ) {
            Box(modifier = Modifier
                .size(25.dp)
                .offset { IntOffset(badgeX.value.toInt(), -30) }
                .onGloballyPositioned {
                    badgeXInRoot.value = it.positionInRoot().x
                }
                .clip(CircleShape)
                .background(Color.White)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(onHorizontalDrag = { change, value ->
                        val newValue = badgeX.value + value
                        if (newValue < size.width) {
                            badgeX.value = newValue
                            positionBallPercentage.value =
                                ((100 * (newValue)) / ((configuration.screenWidthDp) * density.density)).toInt()
                        }
                    })
                })
        }
    }
}