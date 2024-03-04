package com.example.chart.screens

import android.graphics.LinearGradient
import android.graphics.PathEffect
import android.graphics.Shader
import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.max
import com.example.chart.data.getChartData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun NightSleepScreen() {
    val scope = rememberCoroutineScope()
    val chartProgress1 = remember {
        Animatable(0f)
    }
    val chartProgress2 = remember {
        Animatable(0f)
    }
    val gradientProgress = remember {
        Animatable(0f)
    }
    val chartData1 = remember {
        mutableStateOf(getChartData())
    }
    val chartData2 = remember {
        mutableStateOf(getChartData())
    }
    val maxCount = listOf(chartData1.value.count(), chartData2.value.count()).max()

    LaunchedEffect(chartData1.value) {
        launch {
            chartProgress1.animateTo(1f, animationSpec = tween(maxCount * 200))
        }
        launch {
            delay(500)
            chartProgress2.animateTo(1f, animationSpec = tween(maxCount * 200))
        }
        launch {
            delay((maxCount*200).toLong())
            gradientProgress.animateTo(1f, animationSpec = tween(500, easing = LinearEasing))
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Night sleep regularity")
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(
                                CircleShape
                            )
                            .background(Color(0xFFFF9525))
                    )
                    Text(text = "Woke up")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(
                                CircleShape
                            )
                            .background(Color(0xFF9925FF))

                    )
                    Text(text = "Fell asleep")
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            val width = size.width
            val height = size.height
            drawChart(
                chartData1.value,
                height,
                width,
                Color(0xFFFF7A15),
                Color(0xB2FFB47A),
                Color(0x6AFFFFFF),
                gradient = true,
                progress = chartProgress1.value,
                gradientProgress = gradientProgress.value
            )
            drawChart(
                values = chartData2.value,
                height = height,
                width = width,
                color1 = Color(0xFFA952FF),
                color2 = null,
                color3 = null,
                gradient = false,
                gradientProgress = 0f,
                progress = chartProgress2.value
            )


        }
        Divider()
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            repeat(maxCount) {
                Text(text = (it + 1).toString())
            }
        }
        Spacer(modifier = Modifier.padding(16.dp))
        Button(onClick = {
            scope.launch {
                launch {
                    chartProgress1.animateTo(0f, animationSpec = tween(maxCount * 100))
                }
                launch {
                    delay(500)
                    chartProgress2.animateTo(0f, animationSpec = tween(maxCount * 100))
                }
                launch {
                    gradientProgress.animateTo(0f, animationSpec = tween(500, easing = LinearEasing))
                }
            }
            scope.launch {
                delay((maxCount*100).toLong())
                delay(500)
                chartData1.value = getChartData()
                chartData2.value = getChartData()
            }
        }, shape = RoundedCornerShape(8.dp)) {
            Text(text = "Regenerate")
        }

    }
}


fun DrawScope.drawChart(
    values: List<Float>,
    height: Float,
    width: Float,
    color1: Color,
    color2: Color?,
    color3: Color?,
    @FloatRange(0.0, 1.0) progress: Float,
    @FloatRange(0.0, 1.0) gradientProgress: Float,
    gradient: Boolean,
    ballInEndLine:Boolean = false,
    ballInLinePositionPercent:Int? = null
) {
    val max = values.max()
    val finalValues = values.map { (it * height) / max }
    val path = getPath(height, width, finalValues)
    val measure = PathMeasure()
    val segment = Path()
    measure.setPath(path, false)
    measure.getSegment(0f, measure.length * progress, segment)
    drawPath(segment, color = color1, style = Stroke(5f))


  if (ballInEndLine){
      val ballMeasure = PathMeasure()

      ballMeasure.setPath(segment,false)
      ballMeasure.getPosition(ballMeasure.length).also {
          if (!it.isUnspecified){
              segment.apply {
                  drawCircle(
                      color = Color.White,
                      radius = 17f,
                      center = Offset(it.x,it.y),
                      style = Stroke(
                          width = 3f,
                      )
                  )
                  drawCircle(
                      color = color1,
                      radius = 14f,
                      center = Offset(it.x,it.y),
                  )
              }
          }
      }
  }

//    if (ballInLinePositionPercent != null){
//        val ballMeasure = PathMeasure()
//
//        ballMeasure.setPath(segment,false)
//        ballMeasure.getPosition((ballInLinePositionPercent.absoluteValue*ballMeasure.length)/100).also {
//            if (!it.isUnspecified){
//                drawCircle(
//                    color = Color.Green,
//                    radius = 17f,
//                    center = Offset(it.x,it.y),
//                )
//            }
//        }
//    }


    if (gradient) {
        drawGradient(path, height, width, color2!!, color3!!, progress = gradientProgress)
    }
}

fun DrawScope.drawGradient(
    path: Path,
    height: Float,
    width: Float,
    color1: Color,
    color2: Color,
    progress: Float
) {
    drawIntoCanvas {
        it.nativeCanvas.drawPath(android.graphics.Path().apply {
            addPath(path.asAndroidPath())
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }, android.graphics.Paint().apply {
            setShader(
                LinearGradient(
                    0f,
                    0f,
                    0f,
                    height,
                    color1.copy(alpha = color1.alpha * progress).toArgb(),
                    color2.toArgb(),
                    Shader.TileMode.MIRROR
                )
            )
        })
    }
}

fun getPath(height: Float, width: Float, dataPoints: List<Float>): Path {
    val path = Path()
    path.moveTo(0f, height - dataPoints[0])

    for (i in 0 until dataPoints.size - 1) {
        val x1 = (i * (width / (dataPoints.size - 1)))
        val y1 = height - dataPoints[i]
        val x2 = ((i + 1) * (width / (dataPoints.size - 1)))
        val y2 = height - dataPoints[i + 1]

        val cx = (x1 + x2) / 2
        path.cubicTo(cx, y1, cx, y2, x2, y2)
    }

    return path
}

fun SimpleDateFormat.getTotalMinutes(time: Long): Int {
    var formatted = format(Date(time))
    val (hour, minute) = formatted.split(":")
    return hour.toInt() * 60 + minute.toInt()
}