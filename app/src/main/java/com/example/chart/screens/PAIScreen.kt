package com.example.chart.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chart.LocalAppState
import com.example.chart.data.getPAIValues
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PAIScreen() {
    val orange = Color(0xFFFF813C)
    val blue = Color(0xFF2C81FF)
    val cyan = Color(0xFF249668)
    val infiniteTransition = rememberInfiniteTransition()

    val appState = LocalAppState.current

    val state = rememberLazyListState()

    val progress = infiniteTransition.animateFloat(
        initialValue = .2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "animation"
    )

    val data = remember {
        mutableStateOf(getPAIValues())
    }

    val currentValue = remember {
        Animatable(0f)
    }
    LaunchedEffect(Unit) {
        currentValue.animateTo(data.value.first().value, animationSpec = tween(500))
    }

    LaunchedEffect(state.isScrollInProgress) {
        state.layoutInfo.visibleItemsInfo.lastOrNull()?.index?.let {
            val lastVisibleItem = data.value[it]
            data.value = data.value.map {
                launch {
                    currentValue.animateTo(lastVisibleItem.value, animationSpec = tween(500))
                }
                it.copy(selected = it.id == lastVisibleItem.id)
            }
        }

    }

    val sides = 7

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Box(modifier=Modifier.size(200.dp)) {

            Canvas(
                modifier = Modifier
                    .size(200.dp)
                    .rotate(13f)
            ) {
                val cx = size.width / 2
                val cy = size.height / 2

                val size = size.height / 2

                val firstStrokeSize = size + (20f * progress.value) + 10f
                val secondStrokeSize = size + (30f * (1f - progress.value)) + 60f

                val mainPath = getPath(cx, cy, size, sides)
                val mutedProgressPath = getPath(cx, cy, size+20f, sides)
                val strokePath1 = getPath(cx, cy, firstStrokeSize + 30f, sides)
                val strokePath2 = getPath(cx, cy, secondStrokeSize + 20f, sides)

                drawPath(
                    path = mainPath,
                    color = orange
                )
                drawPath(
                    path = mutedProgressPath,
                    color = Color.LightGray,
                    style = Stroke(5f)
                )

                drawPath(
                    path = strokePath1,
                    color = orange.copy(alpha = progress.value),
                    style = Stroke(3f, pathEffect = PathEffect.cornerPathEffect(20f))
                )
                drawPath(
                    path = strokePath2,
                    color = orange.copy(alpha = (1f - progress.value) + .2f),
                    style = Stroke(3f, pathEffect = PathEffect.cornerPathEffect(20f))
                )
            }
            Canvas(
                Modifier
                    .size(200.dp)
                    .rotate(-90f)
                    .graphicsLayer {
                    }) {

                val cx = size.width / 2
                val cy = size.height / 2

                val size = size.height / 2

                val progressPath = getPath(cx, cy, size + 20f, sides,)

                val pathMeasure = PathMeasure()
                pathMeasure.setPath(progressPath, false)
                val progressSegment = Path()
                pathMeasure.getSegment(
                    0f, (currentValue.value * pathMeasure.length) / 100, progressSegment
                )

                drawPath(
                    path = progressSegment,
                    color = orange,
                    style = Stroke(5f)
                )
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = currentValue.value.roundToInt().toString(), fontSize = 32.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "PAI", fontSize = 32.sp, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier
            .fillMaxHeight()
            .weight(1f))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            state = state,
            horizontalArrangement = Arrangement.SpaceAround,
            flingBehavior = rememberSnapFlingBehavior(state)
        ) {
            val dateFormat = SimpleDateFormat("MM/dd")
            itemsIndexed(data.value, key = { index, item -> index }) { index, item ->
                Row {
                    Column(
                        modifier = Modifier.clickable {
                            data.value = data.value.map {
                                appState.scope.launch {
                                    currentValue.animateTo(item.value, animationSpec = tween(500))
                                }
                                it.copy(selected = it.id == item.id)
                            }
                        },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(10.dp)
                                .fillMaxHeight()
                                .weight(1f)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.BottomCenter
                        ) {

                            val barColor = if (item.value >= 80) {
                                cyan
                            } else if (item.value in 50f..80f) {
                                blue
                            } else {
                                orange
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight((item.value / 100.0).toFloat())
                                    .clip(CircleShape)
                                    .background(barColor)
                            )

                        }
                        Text(text = dateFormat.format(Date(item.date)))
                        Icon(
                            modifier = Modifier
                                .rotate(180f)
                                .alpha(if (item.selected) 1f else 0f),
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
    }
}


fun getPath(cx: Float, cy: Float, size: Float, sides: Int): Path {
    val angle = (2 * PI / sides).toFloat()
    val startAngle = angle / 2  // Half of the angle between sides

    val path = Path().apply {
        moveTo(
            cx + (size * cos(startAngle)).toFloat(),
            cy + (size * sin(startAngle)).toFloat()
        )
        for (i in 1 until sides) {
            val x = cx + (size * cos(startAngle + angle * i)).toFloat()
            val y = cy + (size * sin(startAngle + angle * i)).toFloat()
            lineTo(x, y)
        }
        close()
    }
    return path
}