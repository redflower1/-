package com.skibiditoilet.battle.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.9f else 1f, tween(100), label = "scale")
    val cooldownAlpha by animateFloatAsState(if (isOnCooldown) 0.6f else 0f, tween(200), label = "cooldown")

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(enabled = isClickable) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
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

        if (skill.name.isNotEmpty()) {
            Text(
                text = skill.name.take(1),
                color = Color.White,
                fontSize = (size.value * 0.25f).sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

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

        if (!canAfford && !isOnCooldown) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Blue.copy(alpha = 0.3f), CircleShape)
            )
        }
    }
}

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
    val backgroundColor = when {
        !canAfford -> Color.Blue.copy(alpha = 0.3f)
        !isEnabled -> Color.Gray.copy(alpha = 0.5f)
        else -> skill.iconColor.copy(alpha = 0.8f)
    }
    drawCircle(color = backgroundColor, radius = scaledRadius, center = center)

    val borderColor = when {
        !isEnabled -> Color.Gray
        !canAfford -> Color.Blue
        else -> Color.White
    }
    drawCircle(color = borderColor, radius = scaledRadius, center = center, style = Stroke(width = 3.dp.toPx()))

    if (cooldownProgress > 0) {
        drawCircle(Color.Black.copy(alpha = cooldownAlpha), radius = scaledRadius, center = center)
        val sweepAngle = 360f * cooldownProgress
        drawArc(
            color = Color.Red.copy(alpha = 0.7f),
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset(center.x - scaledRadius, center.y - scaledRadius),
            size = androidx.compose.ui.geometry.Size(scaledRadius * 2, scaledRadius * 2)
        )
    }
}

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
        skills.forEach { skill ->
            SkillButton(skill = skill, currentMana = currentMana, onClick = { onSkillClick(skill) })
        }
        Spacer(modifier = Modifier.width(8.dp))
        SkillButton(
            skill = ultimateSkill,
            size = 70.dp,
            currentMana = currentMana,
            onClick = { onSkillClick(ultimateSkill) }
        )
    }
}

@Composable
fun SkillTooltip(skill: Skill, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(skill.name, Color.White, 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(skill.description, Color.Gray, 12.sp)
            Spacer(Modifier.height(8.dp))
            Row {
                Text("法力: ${skill.manaCost.toInt()}", Color.Cyan, 10.sp)
                Spacer(Modifier.width(12.dp))
                Text("冷却: ${skill.cooldownTime / 1000}秒", Color.Yellow, 10.sp)
            }
        }
    }
}
