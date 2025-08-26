package com.skibiditoilet.battle.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skibiditoilet.battle.ui.theme.HealthRed
import com.skibiditoilet.battle.ui.theme.ManaBlue

/**
 * 血条组件
 */
@Composable
fun HealthBar(
    currentHealth: Float,
    maxHealth: Float,
    modifier: Modifier = Modifier,
    height: Dp = 20.dp,
    showText: Boolean = true,
    animationDuration: Int = 500
) {
    val healthPercentage = (currentHealth / maxHealth).coerceIn(0f, 1f)
    
    // 动画血条变化
    val animatedHealthPercentage by animateFloatAsState(
        targetValue = healthPercentage,
        animationSpec = tween(animationDuration),
        label = "health_animation"
    )
    
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(height / 2))
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawHealthBar(
                size = size,
                healthPercentage = animatedHealthPercentage,
                cornerRadius = (height / 2).toPx()
            )
        }
        
        if (showText) {
            Text(
                text = "${currentHealth.toInt()}/${maxHealth.toInt()}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * 法力条组件
 */
@Composable
fun ManaBar(
    currentMana: Float,
    maxMana: Float,
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    showText: Boolean = true,
    animationDuration: Int = 300
) {
    val manaPercentage = (currentMana / maxMana).coerceIn(0f, 1f)
    
    // 动画法力条变化
    val animatedManaPercentage by animateFloatAsState(
        targetValue = manaPercentage,
        animationSpec = tween(animationDuration),
        label = "mana_animation"
    )
    
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(height / 2))
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawManaBar(
                size = size,
                manaPercentage = animatedManaPercentage,
                cornerRadius = (height / 2).toPx()
            )
        }
        
        if (showText) {
            Text(
                text = "${currentMana.toInt()}/${maxMana.toInt()}",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * 组合的血条和法力条
 */
@Composable
fun HealthManaDisplay(
    currentHealth: Float,
    maxHealth: Float,
    currentMana: Float,
    maxMana: Float,
    characterName: String = "",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (characterName.isNotEmpty()) {
            Text(
                text = characterName,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        HealthBar(
            currentHealth = currentHealth,
            maxHealth = maxHealth,
            modifier = Modifier.fillMaxWidth()
        )
        
        ManaBar(
            currentMana = currentMana,
            maxMana = maxMana,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * 敌人血条（简化版，只显示血量）
 */
@Composable
fun EnemyHealthBar(
    currentHealth: Float,
    maxHealth: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp
) {
    val healthPercentage = (currentHealth / maxHealth).coerceIn(0f, 1f)
    
    val animatedHealthPercentage by animateFloatAsState(
        targetValue = healthPercentage,
        animationSpec = tween(300),
        label = "enemy_health_animation"
    )
    
    Canvas(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(height / 2))
    ) {
        drawEnemyHealthBar(
            size = size,
            healthPercentage = animatedHealthPercentage,
            cornerRadius = (height / 2).toPx()
        )
    }
}

/**
 * 绘制血条
 */
private fun DrawScope.drawHealthBar(
    size: Size,
    healthPercentage: Float,
    cornerRadius: Float
) {
    // 背景
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.5f),
        size = size,
        cornerRadius = CornerRadius(cornerRadius)
    )
    
    // 血条渐变
    val healthGradient = Brush.horizontalGradient(
        colors = when {
            healthPercentage > 0.6f -> listOf(Color(0xFF4CAF50), Color(0xFF8BC34A))
            healthPercentage > 0.3f -> listOf(Color(0xFFFF9800), Color(0xFFFFC107))
            else -> listOf(Color(0xFFF44336), Color(0xFFE91E63))
        }
    )
    
    if (healthPercentage > 0) {
        drawRoundRect(
            brush = healthGradient,
            size = Size(size.width * healthPercentage, size.height),
            cornerRadius = CornerRadius(cornerRadius)
        )
    }
    
    // 边框
    drawRoundRect(
        color = Color.White.copy(alpha = 0.8f),
        size = size,
        cornerRadius = CornerRadius(cornerRadius),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
    )
}

/**
 * 绘制法力条
 */
private fun DrawScope.drawManaBar(
    size: Size,
    manaPercentage: Float,
    cornerRadius: Float
) {
    // 背景
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.5f),
        size = size,
        cornerRadius = CornerRadius(cornerRadius)
    )
    
    // 法力条渐变
    val manaGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF2196F3), Color(0xFF03DAC5))
    )
    
    if (manaPercentage > 0) {
        drawRoundRect(
            brush = manaGradient,
            size = Size(size.width * manaPercentage, size.height),
            cornerRadius = CornerRadius(cornerRadius)
        )
    }
    
    // 边框
    drawRoundRect(
        color = Color.White.copy(alpha = 0.8f),
        size = size,
        cornerRadius = CornerRadius(cornerRadius),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
    )
}

/**
 * 绘制敌人血条
 */
private fun DrawScope.drawEnemyHealthBar(
    size: Size,
    healthPercentage: Float,
    cornerRadius: Float
) {
    // 背景
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.7f),
        size = size,
        cornerRadius = CornerRadius(cornerRadius)
    )
    
    // 敌人血条（红色）
    if (healthPercentage > 0) {
        drawRoundRect(
            color = HealthRed,
            size = Size(size.width * healthPercentage, size.height),
            cornerRadius = CornerRadius(cornerRadius)
        )
    }
    
    // 边框
    drawRoundRect(
        color = Color.White.copy(alpha = 0.6f),
        size = size,
        cornerRadius = CornerRadius(cornerRadius),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
    )
}
