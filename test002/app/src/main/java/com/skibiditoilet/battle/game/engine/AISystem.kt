package com.skibiditoilet.battle.game.engine

import androidx.compose.ui.geometry.Offset
import com.skibiditoilet.battle.game.characters.Character
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

/**
 * AI系统 - 控制敌人角色的行为
 */
class AISystem(
    private val gameEngine: GameEngine,
    private val coroutineScope: CoroutineScope
) {
    
    private val aiControllers = mutableMapOf<String, AIController>()
    
    /**
     * 为角色添加AI控制
     */
    fun addAICharacter(character: Character, difficulty: AIDifficulty = AIDifficulty.NORMAL) {
        val controller = AIController(character, gameEngine, difficulty)
        aiControllers[character.id] = controller
        
        // 启动AI循环
        coroutineScope.launch {
            controller.startAI()
        }
    }
    
    /**
     * 移除AI控制
     */
    fun removeAICharacter(characterId: String) {
        aiControllers[characterId]?.stop()
        aiControllers.remove(characterId)
    }
    
    /**
     * 更新所有AI
     */
    fun updateAI(playerCharacter: Character) {
        aiControllers.values.forEach { controller ->
            controller.setTarget(playerCharacter)
        }
    }
    
    /**
     * 停止所有AI
     */
    fun stopAll() {
        aiControllers.values.forEach { it.stop() }
        aiControllers.clear()
    }
}

/**
 * AI控制器 - 控制单个角色的AI行为
 */
class AIController(
    private val character: Character,
    private val gameEngine: GameEngine,
    private val difficulty: AIDifficulty
) {
    private var target: Character? = null
    private var isRunning = false
    private var currentState = AIState.IDLE
    private var lastActionTime = 0L
    
    // AI参数根据难度调整
    private val reactionTime = when (difficulty) {
        AIDifficulty.EASY -> 1500L
        AIDifficulty.NORMAL -> 1000L
        AIDifficulty.HARD -> 500L
    }
    
    private val skillUseProbability = when (difficulty) {
        AIDifficulty.EASY -> 0.3f
        AIDifficulty.NORMAL -> 0.5f
        AIDifficulty.HARD -> 0.8f
    }
    
    /**
     * 设置目标
     */
    fun setTarget(target: Character) {
        this.target = target
    }
    
    /**
     * 启动AI
     */
    suspend fun startAI() {
        isRunning = true
        
        while (isRunning && character.isAlive.value) {
            target?.let { target ->
                if (target.isAlive.value) {
                    updateAI(target)
                }
            }
            
            delay(100) // AI更新频率
        }
    }
    
    /**
     * 停止AI
     */
    fun stop() {
        isRunning = false
    }
    
    /**
     * 更新AI逻辑
     */
    private suspend fun updateAI(target: Character) {
        val currentTime = System.currentTimeMillis()
        
        // 检查是否可以执行新动作
        if (currentTime - lastActionTime < reactionTime) {
            return
        }
        
        val distance = gameEngine.getDistance(character.position.value, target.position.value)
        
        // 状态机逻辑
        when (currentState) {
            AIState.IDLE -> {
                if (distance > character.attackRange * 1.5f) {
                    currentState = AIState.MOVING_TO_TARGET
                } else {
                    currentState = AIState.ATTACKING
                }
            }
            
            AIState.MOVING_TO_TARGET -> {
                moveTowardsTarget(target)
                
                if (distance <= character.attackRange) {
                    currentState = AIState.ATTACKING
                }
            }
            
            AIState.ATTACKING -> {
                performAttackAction(target)
                
                if (distance > character.attackRange * 2f) {
                    currentState = AIState.MOVING_TO_TARGET
                }
            }
            
            AIState.USING_SKILL -> {
                // 技能使用后短暂等待
                delay(500)
                currentState = AIState.IDLE
            }
        }
        
        lastActionTime = currentTime
    }
    
    /**
     * 移动向目标
     */
    private fun moveTowardsTarget(target: Character) {
        val direction = gameEngine.calculateMoveDirection(
            character.position.value,
            target.position.value
        )
        
        // 添加一些随机性，避免AI过于机械
        val randomOffset = Offset(
            (Random.nextFloat() - 0.5f) * 0.3f,
            (Random.nextFloat() - 0.5f) * 0.3f
        )
        
        val finalDirection = direction + randomOffset
        
        // 计算新位置
        val speed = character.movementSpeed * 0.016f
        val newPosition = character.position.value + Offset(
            finalDirection.x * speed,
            finalDirection.y * speed
        )
        
        // 限制在世界边界内
        val clampedPosition = gameEngine.clampToWorld(newPosition)
        character.moveTo(clampedPosition)
        character.setMoving(true)
    }
    
    /**
     * 执行攻击动作
     */
    private suspend fun performAttackAction(target: Character) {
        character.setMoving(false)
        
        // 决定是否使用技能
        if (Random.nextFloat() < skillUseProbability && character.mana.value > 50f) {
            useRandomSkill(target)
        } else {
            // 普通攻击
            gameEngine.performAttack(character, target)
        }
    }
    
    /**
     * 使用随机技能
     */
    private suspend fun useRandomSkill(target: Character) {
        val availableSkills = character.skills.filter { 
            character.canUseSkill(it) 
        }
        
        if (availableSkills.isNotEmpty()) {
            val skill = availableSkills.random()
            character.useSkill(skill, target.position.value)
            currentState = AIState.USING_SKILL
        }
    }
}

/**
 * AI状态枚举
 */
enum class AIState {
    IDLE,               // 空闲
    MOVING_TO_TARGET,   // 移动到目标
    ATTACKING,          // 攻击中
    USING_SKILL,        // 使用技能
    RETREATING          // 撤退
}

/**
 * AI难度枚举
 */
enum class AIDifficulty {
    EASY,       // 简单 - 反应慢，技能使用少
    NORMAL,     // 普通 - 平衡的AI
    HARD        // 困难 - 反应快，技能使用频繁
}
