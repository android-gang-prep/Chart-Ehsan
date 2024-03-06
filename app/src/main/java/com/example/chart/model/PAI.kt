package com.example.chart.model

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D

data class PAI(
    val id:Int,
    val date:Long,
    val value:Float,
    val selected:Boolean = false,
)
