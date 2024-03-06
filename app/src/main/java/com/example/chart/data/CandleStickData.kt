package com.example.chart.data

import com.example.chart.model.CandleStick
import kotlin.random.Random

fun getCandleStickData(): List<CandleStick> {
    val candlesticks = mutableListOf<CandleStick>()
    var previousClose = 100f
    for (i in 1..1000) {
        val open = previousClose
        val close = open + Random.nextFloat() * 10f - 5f
        val high = maxOf(open, close) + Random.nextFloat() * 5f
        val low = minOf(open, close) - Random.nextFloat() * 5f
        previousClose = close
        candlesticks.add(CandleStick(high, open, close, low))
    }
    return candlesticks
}