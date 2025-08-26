package com.skibiditoilet.battle.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skibiditoilet.battle.GameState
import com.skibiditoilet.battle.game.characters.Character
import com.skibiditoilet.battle.ui.components.EnemyHealthBar

/**
 * 游戏场景渲染组件
 * 显示角色、地图背景等
 */
@Composable
fun GameScene(
    gameState: GameState.Playing,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // 背景
        GameBackground()
        
        // 游戏世界Canvas
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // 绘制玩家角色
            val playerPosition = gameState.playerCharacter.position.value
            val playerAlive = gameState.playerCharacter.isAlive.value
            val playerFacing = gameState.playerCharacter.facingDirection.value
            val playerMoving = gameState.playerCharacter.isMoving.value

            if (playerAlive) {
                drawCharacter(
                    position = playerPosition,
                    character = gameState.playerCharacter,
                    isPlayer = true,
                    facingDirection = playerFacing,
                    isMoving = playerMoving
                )
            }

            // 绘制敌人角色
            gameState.enemies.forEach { enemy ->
                val enemyPosition = enemy.position.value
                val enemyAlive = enemy.isAlive.value
                val enemyFacing = enemy.facingDirection.value
                val enemyMoving = enemy.isMoving.value

                if (enemyAlive) {
                    drawCharacter(
                        position = enemyPosition,
                        character = enemy,
                        isPlayer = false,
                        facingDirection = enemyFacing,
                        isMoving = enemyMoving
                    )
                }
            }
        }
        
        // 角色血条覆盖层
        gameState.enemies.forEach { enemy ->
            if (enemy.isAlive.value) {
                CharacterHealthOverlay(
                    character = enemy,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * 游戏背景
 */
@Composable
fun GameBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E4057))
    ) {
        // 简单的网格背景
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSize = 50.dp.toPx()
            val gridColor = Color.White.copy(alpha = 0.1f)
            
            // 绘制垂直线
            var x = 0f
            while (x < size.width) {
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1.dp.toPx()
                )
                x += gridSize
            }
            
            // 绘制水平线
            var y = 0f
            while (y < size.height) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
                y += gridSize
            }
        }
    }
}

/**
 * 绘制角色
 */
private fun DrawScope.drawCharacter(
    position: Offset,
    character: Character,
    isPlayer: Boolean,
    facingDirection: com.skibiditoilet.battle.game.characters.Direction,
    isMoving: Boolean
) {
    val radius = 30.dp.toPx()
    val color = if (isPlayer) character.primaryColor else character.primaryColor.copy(alpha = 0.8f)

    // 绘制角色主体（圆形）
    drawCircle(
        color = color,
        radius = radius,
        center = position
    )

    // 绘制角色边框
    drawCircle(
        color = Color.White,
        radius = radius,
        center = position,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
    )

    // 绘制角色方向指示
    val directionOffset = when (facingDirection) {
        com.skibiditoilet.battle.game.characters.Direction.RIGHT -> Offset(radius * 0.7f, 0f)
        com.skibiditoilet.battle.game.characters.Direction.LEFT -> Offset(-radius * 0.7f, 0f)
        com.skibiditoilet.battle.game.characters.Direction.UP -> Offset(0f, -radius * 0.7f)
        com.skibiditoilet.battle.game.characters.Direction.DOWN -> Offset(0f, radius * 0.7f)
    }

    drawCircle(
        color = Color.Yellow,
        radius = 5.dp.toPx(),
        center = position + directionOffset
    )

    // 如果角色正在移动，绘制移动轨迹
    if (isMoving) {
        drawCircle(
            color = color.copy(alpha = 0.3f),
            radius = radius * 1.2f,
            center = position
        )
    }
}

/**
 * 角色血条覆盖层
 */
@Composable
fun CharacterHealthOverlay(
    character: Character,
    modifier: Modifier = Modifier
) {
    val position by character.position.collectAsState()
    val health by character.health.collectAsState()
    val isAlive by character.isAlive.collectAsState()
    
    if (!isAlive) return
    
    Box(modifier = modifier) {
        // 角色名称和血条
        Column(
            modifier = Modifier
                .offset(
                    x = (position.x - 40).dp,
                    y = (position.y - 60).dp
                )
                .width(80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 角色名称
            Text(
                text = character.name,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            // 血条
            EnemyHealthBar(
                currentHealth = health,
                maxHealth = character.maxHealth,
                modifier = Modifier.fillMaxWidth(),
                height = 6.dp
            )
        }
    }
}

/**
 * 技能特效渲染
 */
@Composable
fun SkillEffectOverlay(
    gameState: GameState.Playing,
    modifier: Modifier = Modifier
) {
    // TODO: 实现技能特效渲染
    // 这里可以添加技能释放时的视觉效果
    Box(modifier = modifier)
}
