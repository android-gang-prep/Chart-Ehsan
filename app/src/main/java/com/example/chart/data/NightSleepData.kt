package com.example.chart.data


fun getChartData(count:Int? = null): List<Float> {
    return MutableList(count ?:(5..10).random()){
        listOf(0f,50f,100f,150f,200f,250f,300f,350f,400f,450f,500f,550f,600f,650f,700f,750f,800f,850f,900f).random()
    }+ mutableListOf(0f)
}