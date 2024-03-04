package com.example.chart.data

import com.example.chart.model.HeartRate
import java.util.Calendar

fun getHeartRateData():List<HeartRate>{
    return MutableList(20){
        HeartRate(
            value = (1..5).random(),
            type = listOf(
                HeartRate.Type.Awake,
                HeartRate.Type.Rem,
                HeartRate.Type.Light,
                HeartRate.Type.Damp
            ).random()
        )
    }
}
fun getHeartRateTime():Pair<Long,Long>{
    return Calendar.getInstance()
        .apply {
            set(Calendar.MONTH,2)
            set(Calendar.DAY_OF_MONTH,28)
            set(Calendar.HOUR_OF_DAY,12)
            set(Calendar.MINUTE,29)
        }.timeInMillis to Calendar.getInstance().apply {
        set(Calendar.MONTH,2)
        set(Calendar.DAY_OF_MONTH,28)
        set(Calendar.HOUR_OF_DAY,2)
        set(Calendar.MINUTE,47)
    }.timeInMillis
}

