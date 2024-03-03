package com.example.chart.model

import androidx.compose.ui.graphics.Color

data class SleepDuration(
    val children: List<SleepDurationChild>,
    val totalMinutes:Int = children.sumOf { it.minuteAmount }
)

data class SleepDurationChild(
    val minuteAmount:Int,
    val type:Type
) {
    enum class Type(val color: Color,val typeName:String) {
        Deep(color = Color(0xFFBF2AFF),typeName = "Deep"),
        Awake(color=  Color(0xFFFFBB13),typeName = "Awake"),
        Light(color = Color(0xFF3FABFF),typeName = "Light"),
        Naps(color = Color(0xFF3FABFF),typeName = "Naps"),
        Rem(color = Color(0xFF197248),typeName = "Rem")
    }
}
