package com.skibiditoilet.battle.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.*

/**
 * 虚拟摇杆组件
 * 类似王者荣耀的移动摇杆
 */
@Composable
fun VirtualJoystick(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    knobSize: Dp = 50.dp,
    onDirectionChanged: (Float, Float) -> Unit = { _, _ -> },
    backgroundColor: Color = Color.Black.copy(alpha = 0.3f),
    knobColor: Color = Color.White.copy(alpha = 0.8f),
    borderColor: Color = Color.White.copy(alpha = 0.5f)
) {
    val density = LocalDensity.current
    val sizePx = with(density) { size.toPx() }
    val knobSizePx = with(density) { knobSize.toPx() }
    val radius = sizePx / 2f
    val knobRadius = knobSizePx / 2f
    val maxDistance = radius - knobRadius
    
    var knobPosition by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            val center = Offset(sizePx / 2f, sizePx / 2f)
                            val distance = (offset - center).getDistance()
                            
                            if (distance <= maxDistance) {
                                knobPosition = offset - center
                            } else {
                                val angle = atan2(
                                    (offset - center).y,
                                    (offset - center).x
                                )
                                knobPosition = Offset(
                                    cos(angle) * maxDistance,
                                    sin(angle) * maxDistance
                                )
                            }
                            
                            // 计算方向值 (-1 到 1)
                            val normalizedX = knobPosition.x / maxDistance
                            val normalizedY = knobPosition.y / maxDistance
                            onDirectionChanged(normalizedX, normalizedY)
                        },
                        onDragEnd = {
                            isDragging = false
                            knobPosition = Offset.Zero
                            onDirectionChanged(0f, 0f)
                        },
                        onDrag = { _, dragAmount ->
                            if (isDragging) {
                                val newPosition = knobPosition + dragAmount
                                val distance = newPosition.getDistance()
                                
                                knobPosition = if (distance <= maxDistance) {
                                    newPosition
                                } else {
                                    val angle = atan2(newPosition.y, newPosition.x)
                                    Offset(
                                        cos(angle) * maxDistance,
                                        sin(angle) * maxDistance
                                    )
                                }
                                
                                // 计算方向值 (-1 到 1)
                                val normalizedX = knobPosition.x / maxDistance
                                val normalizedY = knobPosition.y / maxDistance
                                onDirectionChanged(normalizedX, normalizedY)
                            }
                        }
                    )
                }
        ) {
            drawJoystick(
                center = size.center,
                radius = radius,
                knobPosition = knobPosition,
                knobRadius = knobRadius,
                backgroundColor = backgroundColor,
                knobColor = knobColor,
                borderColor = borderColor,
                isDragging = isDragging
            )
        }
    }
}

/**
 * 绘制摇杆
 */
private fun DrawScope.drawJoystick(
    center: Offset,
    radius: Float,
    knobPosition: Offset,
    knobRadius: Float,
    backgroundColor: Color,
    knobColor: Color,
    borderColor: Color,
    isDragging: Boolean
) {
    // 绘制背景圆圈
    drawCircle(
        color = backgroundColor,
        radius = radius,
        center = center
    )
    
    // 绘制边框
    drawCircle(
        color = borderColor,
        radius = radius,
        center = center,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
    )
    
    // 绘制摇杆旋钮
    val knobCenter = center + knobPosition
    val knobAlpha = if (isDragging) 1f else 0.8f
    
    drawCircle(
        color = knobColor.copy(alpha = knobAlpha),
        radius = knobRadius,
        center = knobCenter
    )
    
    // 绘制旋钮边框
    drawCircle(
        color = borderColor,
        radius = knobRadius,
        center = knobCenter,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
    )
    
    // 如果正在拖拽，绘制方向指示线
    if (isDragging && knobPosition.getDistance() > 0) {
        drawLine(
            color = borderColor,
            start = center,
            end = knobCenter,
            strokeWidth = 2.dp.toPx()
        )
    }
}

/**
 * 摇杆数据类
 */
data class JoystickDirection(
    val x: Float,
    val y: Float,
    val magnitude: Float = sqrt(x * x + y * y).coerceAtMost(1f),
    val angle: Float = atan2(y, x)
) {
    val isActive: Boolean get() = magnitude > 0.1f
    
    companion object {
        val ZERO = JoystickDirection(0f, 0f)
    }
}
