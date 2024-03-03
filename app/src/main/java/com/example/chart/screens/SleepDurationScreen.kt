package com.example.chart.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chart.data.getSleepDurationData
import com.example.chart.model.SleepDuration
import com.example.chart.model.SleepDurationChild
import kotlinx.coroutines.delay

fun MutableState<List<SleepDuration>>.regenerate(){
    value = getSleepDurationData()
}

@Composable
fun SleepDurationScreen() {

    val data = remember {
        mutableStateOf(getSleepDurationData())
    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Column(modifier=Modifier.padding(16.dp)) {
            Text(text = "Sleep duration per month")
            Spacer(modifier = Modifier.height(22.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {

                val totalMinutes = data.value.sumOf { it.totalMinutes }
                val hours = totalMinutes/60
                val minutes = totalMinutes%60
                Text(text = hours.toString(), fontSize = 30.sp)
                Text(text = "hr", color = Color.DarkGray)
                Spacer(modifier = Modifier.width(22.dp))
                Text(text = minutes.toString(), fontSize = 30.sp)
                Text(text = "min", color = Color.DarkGray)
            }
            Text(text = "2024", color = Color.DarkGray)
            Spacer(modifier = Modifier.height(12.dp))
        }
        SleepDurationChart(data.value)
        Spacer(modifier = Modifier.height(8.dp))
        Divider()
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(modifier= Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.SpaceBetween, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            val items = SleepDurationChild.Type.entries
            items(items){
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ){
                    Box(modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(it.color)
                    )
                    Text(text = it.typeName)
                }
            }
        }

        Spacer(modifier = Modifier.height(64.dp))
        Column(modifier=Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = data::regenerate, shape = RoundedCornerShape(8.dp)) {
                Text(text = "Regenerate")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                itemsIndexed(data.value){index,sleepDuration->
                    Column {
                        Text(text = "Bar ${index+1}", fontWeight = FontWeight.Bold)
                        LazyRow {
                            items(sleepDuration.children){
                                Text(text = "-> ${it.minuteAmount/60}H, ${it.minuteAmount%60}M = ${it.type.typeName} |",modifier= Modifier.padding(start = 18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SleepDurationChart(data:List<SleepDuration>) {
    val maxBarMinute = data.maxByOrNull { it.totalMinutes }!!.totalMinutes

    val density = LocalDensity.current

    val barsParentHeight = remember {
        mutableIntStateOf(0)
    }
    val barsParentHeightDp = barsParentHeight.intValue / density.density

    val chartEndPadding = 38.dp
    
    Column(modifier= Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp)) {
        Box(
            modifier = Modifier
                .height(300.dp)
                .onSizeChanged {
                    barsParentHeight.intValue = it.height
                },
        ) {
            Column(modifier= Modifier
                .fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                ChartDivider(text = ((data.maxByOrNull { it.totalMinutes }?.totalMinutes ?: 0)/60).toString()+"h")
                ChartDivider(text = ("%.2f".format((data.map { it.totalMinutes }.average()/60))+"h"))
                Row {}
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = chartEndPadding), verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                data.forEachIndexed { index,duration ->
                    val barHeight = (barsParentHeightDp * duration.totalMinutes) / maxBarMinute
                    Column(
                        modifier = Modifier
                            .height(barHeight.dp)
                            .width(20.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        duration.children.forEach {
                            val childHeight = (barHeight * it.minuteAmount) / duration.totalMinutes
                            AnimateBar(delay = index*100, content = {
                                Box(
                                    modifier = Modifier
                                        .height(childHeight.dp)
                                        .fillMaxWidth()
                                        .background(it.type.color), contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "${it.minuteAmount/60}H", color = Color.White, fontSize = 9.sp)
                                }
                            })
                        }
                    }
                }
            }
        }
        ChartDivider(text = "0h", endPadding = 8.dp)
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(end = chartEndPadding)
            .offset { IntOffset(10, -30) }, horizontalArrangement = Arrangement.SpaceBetween) {
            repeat(data.count()){
                Text(modifier=Modifier.width(20.dp), textAlign = TextAlign.Center,text = (it+1).toString(), color = Color.Gray)
            }
        }
    }
}

@Composable
fun ChartDivider(
    text:String,
    endPadding:Dp = 0.dp,
) {
    Row(
        modifier=Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Divider(
            modifier= Modifier
                .weight(1f)
                .fillMaxWidth(.75f)
                .padding(end = endPadding),

            )
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = text,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier=Modifier.offset{IntOffset(20,-25)}
        )
    }
}


@Composable
fun AnimateBar(content: @Composable () -> Unit,delay:Int) {
    val visible = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        delay(delay.toLong())
        visible.value = true
    }
    AnimatedVisibility(
        visible = visible.value, enter = expandVertically(tween(500)), exit = shrinkVertically(
            tween(400)
        )
    ) {
        content()
    }
}