package com.skibiditoilet.battle

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skibiditoilet.battle.game.characters.Character
import com.skibiditoilet.battle.game.characters.CharacterFactory
import com.skibiditoilet.battle.game.characters.CharacterType
import com.skibiditoilet.battle.game.engine.AISystem
import com.skibiditoilet.battle.game.engine.AIDifficulty
import com.skibiditoilet.battle.game.engine.GameEngine
import com.skibiditoilet.battle.game.skills.Skill
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 游戏状态管理ViewModel
 */
class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow<GameState>(GameState.Loading)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var gameLoopJob: Job? = null
    private var currentGameMode: String = "single"

    // 游戏引擎和AI系统
    private val gameEngine = GameEngine()
    private val aiSystem = AISystem(gameEngine, viewModelScope)
    
    /**
     * 初始化游戏
     */
    fun initializeGame(gameMode: String) {
        currentGameMode = gameMode
        viewModelScope.launch {
            _gameState.value = GameState.Loading
            
            // 模拟加载时间
            delay(2000)
            
            // 创建玩家角色（默认选择马桶人）
            val playerCharacter = CharacterFactory.createCharacter(CharacterType.TOILET_MAN)
            playerCharacter.moveTo(Offset(200f, 400f)) // 设置初始位置
            
            // 创建敌人角色
            val enemies = when (gameMode) {
                "single" -> listOf(
                    CharacterFactory.createCharacter(CharacterType.CAMERA_MAN).apply {
                        moveTo(Offset(800f, 400f))
                    }
                )
                else -> listOf(
                    CharacterFactory.createCharacter(CharacterType.CAMERA_MAN).apply {
                        moveTo(Offset(700f, 300f))
                    },
                    CharacterFactory.createCharacter(CharacterType.TV_MAN).apply {
                        moveTo(Offset(800f, 500f))
                    }
                )
            }

            // 为敌人添加AI控制
            enemies.forEach { enemy ->
                aiSystem.addAICharacter(enemy, AIDifficulty.NORMAL)
            }
            
            _gameState.value = GameState.Playing(
                playerCharacter = playerCharacter,
                enemies = enemies,
                gameTime = 0L,
                score = 0
            )
            
            // 启动游戏循环
            startGameLoop()
        }
    }
    
    /**
     * 启动游戏循环
     */
    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (true) {
                val currentState = _gameState.value
                if (currentState is GameState.Playing) {
                    updateGame(currentState)
                    
                    // 检查游戏结束条件
                    checkGameEndConditions(currentState)
                }
                delay(16) // 约60 FPS
            }
        }
    }
    
    /**
     * 更新游戏状态
     */
    private fun updateGame(gameState: GameState.Playing) {
        // 更新游戏时间
        val newGameTime = gameState.gameTime + 16

        // 更新角色状态（法力恢复等）
        updateCharacterStates(gameState)

        // 更新AI行为
        aiSystem.updateAI(gameState.playerCharacter)

        _gameState.value = gameState.copy(gameTime = newGameTime)
    }
    
    /**
     * 更新角色状态
     */
    private fun updateCharacterStates(gameState: GameState.Playing) {
        // 玩家角色法力恢复
        if (gameState.playerCharacter.isAlive.value) {
            gameState.playerCharacter.restoreMana(1f) // 每帧恢复1点法力
        }
        
        // 敌人角色状态更新
        gameState.enemies.forEach { enemy ->
            if (enemy.isAlive.value) {
                enemy.restoreMana(0.5f)
            }
        }
    }
    

    
    /**
     * 检查游戏结束条件
     */
    private fun checkGameEndConditions(gameState: GameState.Playing) {
        val playerAlive = gameState.playerCharacter.isAlive.value
        val enemiesAlive = gameState.enemies.any { it.isAlive.value }
        
        when {
            !playerAlive -> {
                _gameState.value = GameState.GameOver(isVictory = false)
                gameLoopJob?.cancel()
                aiSystem.stopAll()
            }
            !enemiesAlive -> {
                _gameState.value = GameState.GameOver(isVictory = true)
                gameLoopJob?.cancel()
                aiSystem.stopAll()
            }
        }
    }
    
    /**
     * 处理移动输入
     */
    fun handleMovementInput(x: Float, y: Float) {
        val currentState = _gameState.value
        if (currentState is GameState.Playing && currentState.playerCharacter.isAlive.value) {
            val character = currentState.playerCharacter
            val currentPos = character.position.value
            
            // 计算新位置
            val speed = character.movementSpeed * 0.016f // 转换为每帧移动距离
            val newX = currentPos.x + x * speed
            val newY = currentPos.y + y * speed

            // 使用游戏引擎的边界检查
            val newPosition = gameEngine.clampToWorld(Offset(newX, newY))
            character.moveTo(newPosition)
            character.setMoving(x != 0f || y != 0f)
        }
    }
    
    /**
     * 使用技能
     */
    fun useSkill(skill: Skill) {
        val currentState = _gameState.value
        if (currentState is GameState.Playing) {
            val character = currentState.playerCharacter
            
            viewModelScope.launch {
                if (character.canUseSkill(skill)) {
                    // 寻找最近的敌人作为目标
                    val nearestEnemy = gameEngine.findNearestEnemy(character, currentState.enemies)
                    val target = nearestEnemy?.position?.value

                    character.useSkill(skill, target)
                }
            }
        }
    }
    
    /**
     * 执行普通攻击
     */
    fun performAttack() {
        val currentState = _gameState.value
        if (currentState is GameState.Playing) {
            val character = currentState.playerCharacter
            
            if (character.isAlive.value) {
                // 寻找攻击范围内的敌人
                val targetEnemy = gameEngine.findNearestEnemy(character, currentState.enemies)

                if (targetEnemy != null && character.isInAttackRange(targetEnemy.position.value)) {
                    // 使用游戏引擎执行攻击
                    gameEngine.performAttack(character, targetEnemy)
                }
            }
        }
    }
    

    
    /**
     * 重新开始游戏
     */
    fun restartGame() {
        gameLoopJob?.cancel()
        aiSystem.stopAll()
        initializeGame(currentGameMode)
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
        aiSystem.stopAll()
    }
}

/**
 * 游戏状态密封类
 */
sealed class GameState {
    object Loading : GameState()
    
    data class Playing(
        val playerCharacter: Character,
        val enemies: List<Character>,
        val gameTime: Long,
        val score: Int
    ) : GameState()
    
    data class GameOver(
        val isVictory: Boolean
    ) : GameState()
}
