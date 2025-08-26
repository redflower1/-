package com.skibiditoilet.battle.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skibiditoilet.battle.game.characters.Character
import com.skibiditoilet.battle.game.skills.Skill
import com.skibiditoilet.battle.ui.components.*

/**
 * 游戏主UI界面
 * 类似王者荣耀的布局
 */
@Composable
fun GameUI(
    playerCharacter: Character,
    onMovementInput: (Float, Float) -> Unit,
    onSkillUse: (Skill) -> Unit,
    onAttackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val health by playerCharacter.health.collectAsState()
    val mana by playerCharacter.mana.collectAsState()
    val isAlive by playerCharacter.isAlive.collectAsState()
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 左下角 - 虚拟摇杆
        VirtualJoystick(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(32.dp),
            onDirectionChanged = onMovementInput
        )
        
        // 右下角 - 技能栏
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // 攻击按钮
            AttackButton(
                onClick = onAttackClick,
                isEnabled = isAlive
            )
            
            // 技能按钮
            SkillBar(
                skills = playerCharacter.skills,
                ultimateSkill = playerCharacter.ultimateSkill,
                currentMana = mana,
                onSkillClick = onSkillUse
            )
        }
        
        // 左上角 - 玩家状态
        HealthManaDisplay(
            currentHealth = health,
            maxHealth = playerCharacter.maxHealth,
            currentMana = mana,
            maxMana = playerCharacter.maxMana,
            characterName = playerCharacter.name,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .width(200.dp)
        )
        
        // 右上角 - 游戏信息
        GameInfoPanel(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )
        
        // 中上方 - 小地图（占位）
        MiniMap(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .size(120.dp)
        )
        
        // 如果角色死亡，显示复活界面
        if (!isAlive) {
            RespawnOverlay(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * 攻击按钮
 */
@Composable
fun AttackButton(
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier.size(70.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red.copy(alpha = 0.8f),
            disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = "攻击",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 游戏信息面板
 */
@Composable
fun GameInfoPanel(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "游戏时间",
                color = Color.White,
                fontSize = 12.sp
            )
            
            // TODO: 实际游戏时间
            Text(
                text = "05:30",
                color = Color.Yellow,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "击杀数",
                color = Color.White,
                fontSize = 12.sp
            )
            
            // TODO: 实际击杀数
            Text(
                text = "3",
                color = Color.Green,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 小地图组件（占位）
 */
@Composable
fun MiniMap(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "小地图",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

/**
 * 复活界面
 */
@Composable
fun RespawnOverlay(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "你已阵亡",
                color = Color.Red,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "复活倒计时",
                color = Color.White,
                fontSize = 16.sp
            )
            
            // TODO: 实际复活倒计时
            Text(
                text = "8",
                color = Color.Yellow,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { /* TODO: 复活逻辑 */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green
                )
            ) {
                Text(
                    text = "立即复活 (消耗金币)",
                    color = Color.White
                )
            }
        }
    }
}
