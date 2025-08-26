package com.skibiditoilet.battle.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skibiditoilet.battle.game.skills.Skill

/**
 * 技能按钮组件
 * 显示技能图标、冷却时间和法力消耗
 */
@Composable
fun SkillButton(
    skill: Skill,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    isEnabled: Boolean = true,
    currentMana: Float = 0f,
    onClick: () -> Unit = {}
) {
    val density = LocalDensity.current
    val sizePx = with(density) { size.toPx() }
    
    val isOnCooldown by skill.isOnCooldown.collectAsState()
    val cooldownProgress by skill.cooldownProgress.collectAsState()
    val canAfford = currentMana >= skill.manaCost
    val isClickable = isEnabled && !isOnCooldown && canAfford
    
    // 按钮按下动画
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(100),
        label = "button_scale"
    )
    
    // 冷却动画
    val cooldownAlpha by animateFloatAsState(
        targetValue = if (isOnCooldown) 0.6f else 0f,
        animationSpec = tween(200),
        label = "cooldown_alpha"
    )
    
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(enabled = isClickable) {
                if (isClickable) {
                    onClick()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawSkillButton(
                center = size.center,
                radius = sizePx / 2f,
                skill = skill,
                isEnabled = isClickable,
                cooldownProgress = cooldownProgress,
                cooldownAlpha = cooldownAlpha,
                scale = scale,
                canAfford = canAfford
            )
        }
        
        // 技能快捷键文字（如果有）
        if (skill.name.isNotEmpty()) {
            Text(
                text = skill.name.take(1), // 显示技能名称首字母
                color = Color.White,
                fontSize = (size.value * 0.25f).sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        
        // 冷却时间倒计时
        if (isOnCooldown) {
            val remainingTime = (cooldownProgress * skill.cooldownTime / 1000f).toInt()
            if (remainingTime > 0) {
                Text(
                    text = remainingTime.toString(),
                    color = Color.White,
                    fontSize = (size.value * 0.2f).sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        
        // 法力不足指示
        if (!canAfford && !isOnCooldown) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Blue.copy(alpha = 0.3f), CircleShape)
            )
        }
    }
}

/**
 * 绘制技能按钮
 */
private fun DrawScope.drawSkillButton(
    center: Offset,
    radius: Float,
    skill: Skill,
    isEnabled: Boolean,
    cooldownProgress: Float,
    cooldownAlpha: Float,
    scale: Float,
    canAfford: Boolean
) {
    val scaledRadius = radius * scale
    
    // 绘制背景圆圈
    val backgroundColor = when {
        !canAfford -> Color.Blue.copy(alpha = 0.3f)
        !isEnabled -> Color.Gray.copy(alpha = 0.5f)
        else -> skill.iconColor.copy(alpha = 0.8f)
    }
    
    drawCircle(
        color = backgroundColor,
        radius = scaledRadius,
        center = center
    )
    
    // 绘制边框
    val borderColor = when {
        !isEnabled -> Color.Gray
        !canAfford -> Color.Blue
        else -> Color.White
    }
    
    drawCircle(
        color = borderColor,
        radius = scaledRadius,
        center = center,
        style = Stroke(width = 3.dp.toPx())
    )
    
    // 绘制冷却遮罩
    if (cooldownProgress > 0) {
        drawCircle(
            color = Color.Black.copy(alpha = cooldownAlpha),
            radius = scaledRadius,
            center = center
        )
        
        // 绘制冷却进度弧
        val sweepAngle = 360f * cooldownProgress
        drawArc(
            color = Color.Red.copy(alpha = 0.7f),
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset(
                center.x - scaledRadius,
                center.y - scaledRadius
            ),
            size = androidx.compose.ui.geometry.Size(
                scaledRadius * 2,
                scaledRadius * 2
            )
        )
    }
}

/**
 * 技能栏组件
 * 包含多个技能按钮的水平排列
 */
@Composable
fun SkillBar(
    skills: List<Skill>,
    ultimateSkill: Skill,
    currentMana: Float,
    modifier: Modifier = Modifier,
    onSkillClick: (Skill) -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 普通技能
        skills.forEach { skill ->
            SkillButton(
                skill = skill,
                currentMana = currentMana,
                onClick = { onSkillClick(skill) }
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // 大招按钮（稍大一些）
        SkillButton(
            skill = ultimateSkill,
            size = 70.dp,
            currentMana = currentMana,
            onClick = { onSkillClick(ultimateSkill) }
        )
    }
}

/**
 * 技能信息提示
 */
@Composable
fun SkillTooltip(
    skill: Skill,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = skill.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = skill.description,
                color = Color.Gray,
                fontSize = 12.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row {
                Text(
                    text = "法力: ${skill.manaCost.toInt()}",
                    color = Color.Cyan,
                    fontSize = 10.sp
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "冷却: ${skill.cooldownTime / 1000}秒",
                    color = Color.Yellow,
                    fontSize = 10.sp
                )
            }
        }
    }
}
