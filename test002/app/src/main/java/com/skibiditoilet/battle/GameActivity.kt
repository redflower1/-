package com.skibiditoilet.battle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skibiditoilet.battle.game.characters.CharacterFactory
import com.skibiditoilet.battle.game.characters.CharacterType
import com.skibiditoilet.battle.ui.game.GameUI
import com.skibiditoilet.battle.ui.game.GameScene
import com.skibiditoilet.battle.ui.theme.SkibidiToiletBattleTheme

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val gameMode = intent.getStringExtra("gameMode") ?: "single"
        
        setContent {
            SkibidiToiletBattleTheme {
                GameScreen(gameMode = gameMode)
            }
        }
    }
}

@Composable
fun GameScreen(
    gameMode: String,
    gameViewModel: GameViewModel = viewModel()
) {
    val gameState by gameViewModel.gameState.collectAsState()
    
    LaunchedEffect(gameMode) {
        gameViewModel.initializeGame(gameMode)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when (gameState) {
            is GameState.Loading -> {
                LoadingScreen()
            }
            is GameState.Playing -> {
                // 游戏场景渲染（背景层）
                GameScene(
                    gameState = gameState,
                    modifier = Modifier.fillMaxSize()
                )

                // UI层（前景）
                GameUI(
                    playerCharacter = gameState.playerCharacter,
                    onMovementInput = { x, y ->
                        gameViewModel.handleMovementInput(x, y)
                    },
                    onSkillUse = { skill ->
                        gameViewModel.useSkill(skill)
                    },
                    onAttackClick = {
                        gameViewModel.performAttack()
                    }
                )
            }
            is GameState.GameOver -> {
                GameOverScreen(
                    isVictory = gameState.isVictory,
                    onRestart = { gameViewModel.restartGame() },
                    onExit = { /* TODO: 退出游戏 */ }
                )
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White
        )
    }
}

@Composable
fun GameScene(
    gameState: GameState.Playing,
    modifier: Modifier = Modifier
) {
    // TODO: 实现游戏场景渲染
    // 这里将包含：
    // 1. 地图背景
    // 2. 角色渲染
    // 3. 特效渲染
    // 4. 碰撞检测可视化
    Box(modifier = modifier) {
        // 占位内容
    }
}

@Composable
fun GameOverScreen(
    isVictory: Boolean,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isVictory) "胜利!" else "失败!",
                color = if (isVictory) Color.Green else Color.Red,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green
                )
            ) {
                Text(
                    text = "再来一局",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onExit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text(
                    text = "退出游戏",
                    color = Color.White
                )
            }
        }
    }
}
