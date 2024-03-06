package com.example.chart.data

import com.example.chart.model.PAI
import java.time.LocalDate
import java.util.Calendar

fun getPAIValues():List<PAI>{
    val calendar = Calendar.getInstance()

    return MutableList(15){
        calendar.add(Calendar.DAY_OF_YEAR,1)
        PAI(
            id = it,
            selected = it == 0,
            date = calendar.timeInMillis,
            value = (1..100).random().toFloat()
        )
    }
}