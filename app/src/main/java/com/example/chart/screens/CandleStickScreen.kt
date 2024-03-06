package com.example.chart.screens

import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chart.LocalAppState
import com.example.chart.MainActivity
import com.example.chart.Network
import com.example.chart.NetworkApi
import com.example.chart.data.getCandleStickData
import com.example.chart.model.CandleStick
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

@Composable
fun CandleStickScreen(crypto: String) {

    val appState = LocalAppState.current
    val lifecycleOwner = LocalLifecycleOwner.current



    DisposableEffect(lifecycleOwner) {
        (appState.navController.context as MainActivity)
            .requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            (appState.navController.context as MainActivity)
                .requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    val candles = remember {
        mutableStateOf(emptyList<CandleStick>())
    }

    val takeOffset = remember {
        mutableStateOf(0)
    }

    val takeCandlesPerOffset = 100
    val data =
        candles.value.filterIndexed { index, candleStick -> index in takeOffset.value..takeCandlesPerOffset + takeOffset.value }
    val lastCandle = data.lastOrNull()

    val maxPrice = data.maxOfOrNull { it.high } ?: 0f
    val minPrice = data.minOfOrNull { it.low } ?: 0f
    val priceRange = maxPrice - minPrice

    val padding = 20f

    val fontFamilyResolver = LocalFontFamilyResolver.current
    val layoutDirection = LocalLayoutDirection.current
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        Network.setup()
        runCatching {
            Network.instance.create(NetworkApi::class.java)
                .getCandles(crypto + "USDT", interval = if (crypto == "TRX") "1h" else "4h")
        }
            .onSuccess {
                candles.value = emptyList()
                it.body()?.let {
                    candles.value = it.map {
                        CandleStick(
                            it[2].toFloat(),
                            it[4].toFloat(),
                            it[1].toFloat(),
                            it[3].toFloat()
                        )
                    }
                }
            }
            .onFailure {
                println(it.message)
            }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AnimatedVisibility(
            visible = data.isEmpty(),
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            CircularProgressIndicator(color = Color(0xFF2890FF))
        }
        AnimatedVisibility(visible = data.isNotEmpty()) {
            Row {
                Canvas(modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            println(dragAmount)
                            val increaseValue = if (dragAmount.absoluteValue.toInt() in 0..2) {
                                (-(dragAmount.toInt()))
                            } else {
                                (-(dragAmount.toInt() / 6))
                            }
                            val newTakeOffset = takeOffset.value + increaseValue
                            if (newTakeOffset > 0 && newTakeOffset < candles.value.count() - takeCandlesPerOffset) {
                                takeOffset.value = newTakeOffset
                            }
                        }
                    }) {
                    val width = size.width
                    val height = size.height
                    val chartWidth = width - padding * 2
                    val chartHeight = height - padding * 2
                    val candleWidth = chartWidth / data.size


                    // Draw horizontal grid lines
                    val horizontalLines = 10
                    val gridLineInterval = priceRange / horizontalLines
                    for (i in 0..horizontalLines) {
                        val price = minPrice + i * gridLineInterval
                        val y = height - ((price - minPrice) / priceRange * chartHeight + padding)
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(padding, y),
                            end = Offset(width - padding, y),
                            strokeWidth = 1f
                        )
                    }

                    val verticalLines = 7
                    for (i in 0..verticalLines) {

                        drawLine(
                            color = Color.LightGray,
                            start = Offset((width / verticalLines) * i, padding),
                            end = Offset((width / verticalLines) * i, size.height - padding),
                            strokeWidth = 1f
                        )
                    }

                    data.forEachIndexed { index, candlestick ->
                        val x = padding + index * candleWidth + candleWidth / 2
                        val highY =
                            height - ((candlestick.high - minPrice) / priceRange * chartHeight + padding)
                        val lowY =
                            height - ((candlestick.low - minPrice) / priceRange * chartHeight + padding)
                        val openY =
                            height - ((candlestick.open - minPrice) / priceRange * chartHeight + padding)
                        val closeY =
                            height - ((candlestick.close - minPrice) / priceRange * chartHeight + padding)

                        val candleColor =
                            if (candlestick.close >= candlestick.open) Color(
                                0xF3FF4040
                            ) else Color(0xFF05CC48)
                        drawRect(
                            color = candleColor,
                            topLeft = Offset(x - candleWidth / 4, min(openY, closeY)),
                            size = Size(candleWidth / 2, abs(openY - closeY))
                        )

                        // Draw wicks
                        drawLine(
                            color = candleColor,
                            start = Offset(x, highY),
                            end = Offset(x, min(openY, closeY)),
                            strokeWidth = 2f
                        )
                        drawLine(
                            color = candleColor,
                            start = Offset(x, max(openY, closeY)),
                            end = Offset(x, lowY),
                            strokeWidth = 2f
                        )
                    }


                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        val divisionCount = 5
                        for (i in divisionCount downTo 1) {
                            val percent = (i * 100) / divisionCount
                            val result = minPrice + priceRange * (percent / 100.0)
                            val priceFormatter = getPriceFormatter(priceRange)
                            Text(
                                text = priceFormatter.format(result),
                                color = Color.DarkGray,
                                fontSize = 13.sp
                            )
                        }
                    }
                    if (lastCandle != null) {
                        val lastCandleColor =
                            if (lastCandle.close >= lastCandle.open) Color(
                                0xF3FF4040
                            ) else Color(0xFF05CC48)
                        BoxWithConstraints(modifier = Modifier.fillMaxHeight()) {
                            val height = maxHeight.value * density.density
                            val chartHeight = height - padding * 2

                            val openY =
                                height - ((lastCandle.open - minPrice) / priceRange * chartHeight + padding)
                            val closeY =
                                height - ((lastCandle.close - minPrice) / priceRange * chartHeight + padding)
                            val badgeY = animateIntAsState(targetValue = min(openY, closeY).toInt(), animationSpec = tween(150))
                            Row(
                                modifier = Modifier.offset {
                                    IntOffset(
                                        (-40), badgeY.value
                                    )
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .rotate(90f)
                                        .size(30.dp),
                                    tint = lastCandleColor
                                )
                                Box(
                                    modifier = Modifier
                                        .offset((-13).dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(lastCandleColor)
                                        .padding(2.dp), contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = lastCandle.close.toString(),
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                }

                            }
                        }

                    }
                }
            }
        }
    }
}

fun getPriceFormatter(range: Float): String {
    return if (range > 10) {
        "%.2f"
    } else if (range > 1) {
        "%.4f"
    } else {
        "%.6f"
    }
}