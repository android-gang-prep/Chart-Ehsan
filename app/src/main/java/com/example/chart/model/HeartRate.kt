package com.example.chart.model

import androidx.compose.ui.graphics.Color

data class HeartRate(
    val value:Int,
    val type:Type
){
    enum class Type(
        val color: Color
    ){
        Awake(Color(0xFFFF823E)),
        Rem(Color(0xCD41C9FF)),
        Light(Color(0xFF1D9FD3)),
        Damp(Color(0xFF9F3EFF))
    }
}
