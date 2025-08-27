package com.skibiditoilet.battle.game.engine

import androidx.compose.ui.geometry.Offset
import com.skibiditoilet.battle.game.characters.Character
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * AI 系统 - 敌人行为逻辑
 */
class AISystem(
    private val gameEngine: GameEngine,
    private val coroutineScope: CoroutineScope
) {
    private val aiControllers = mutableMapOf<String, AIController>()

    fun addAICharacter(character: Character, difficulty: AIDifficulty = AIDifficulty.NORMAL) {
        val controller = AIController(character, gameEngine, difficulty)
        aiControllers[character.id] = controller

        coroutineScope.launch { controller.startAI() }
    }

    fun removeAICharacter(characterId: String) {
        aiControllers[characterId]?.stop()
        aiControllers.remove(characterId)
    }

    fun updateAI(playerCharacter: Character) {
        aiControllers.values.forEach { it.setTarget(playerCharacter) }
    }

    fun stopAll() {
        aiControllers.values.forEach { it.stop() }
        aiControllers.clear()
    }
}

class AIController(
    private val character: Character,
    private val gameEngine: GameEngine,
    private val difficulty: AIDifficulty
) {
    private var target: Character? = null
    private var isRunning = false
    private var currentState = AIState.IDLE
    private var lastActionTime = 0L

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

    fun setTarget(target: Character) { this.target = target }

    suspend fun startAI() {
        isRunning = true
        while (isRunning && character.isAlive.value) {
            target?.takeIf { it.isAlive.value }?.let { updateAI(it) }
            delay(100)
        }
    }

    fun stop() { isRunning = false }

    private suspend fun updateAI(target: Character) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastActionTime < reactionTime) return

        val distance = gameEngine.getDistance(character.position.value, target.position.value)

        when (currentState) {
            AIState.IDLE -> {
                currentState = if (distance > character.attackRange * 1.5f) AIState.MOVING_TO_TARGET else AIState.ATTACKING
            }
            AIState.MOVING_TO_TARGET -> {
                moveTowardsTarget(target)
                if (distance <= character.attackRange) currentState = AIState.ATTACKING
            }
            AIState.ATTACKING -> {
                performAttackAction(target)
                if (distance > character.attackRange * 2f) currentState = AIState.MOVING_TO_TARGET
            }
            AIState.USING_SKILL -> {
                delay(500)
                currentState = AIState.IDLE
            }
            AIState.RETREATING -> {
                // TODO: 撤退逻辑
                currentState = AIState.IDLE
            }
        }

        lastActionTime = currentTime
    }

    private fun moveTowardsTarget(target: Character) {
        val direction = gameEngine.calculateMoveDirection(character.position.value, target.position.value)
        val randomOffset = Offset((Random.nextFloat() - 0.5f) * 0.3f, (Random.nextFloat() - 0.5f) * 0.3f)
        val finalDirection = direction + randomOffset
        val speed = character.movementSpeed * 0.016f
        val newPosition = character.position.value + Offset(finalDirection.x * speed, finalDirection.y * speed)
        val clampedPosition = gameEngine.clampToWorld(newPosition)
        character.moveTo(clampedPosition)
        character.setMoving(true)
    }

    private suspend fun performAttackAction(target: Character) {
        character.setMoving(false)
        if (Random.nextFloat() < skillUseProbability && character.mana.value > 50f) {
            useRandomSkill(target)
        } else {
            gameEngine.performAttack(character, target)
        }
    }

    private suspend fun useRandomSkill(target: Character) {
        val availableSkills = character.skills.filter { character.canUseSkill(it) }
        if (availableSkills.isNotEmpty()) {
            val skill = availableSkills.random()
            character.useSkill(skill, target.position.value)
            currentState = AIState.USING_SKILL
        }
    }
}

enum class AIState {
    IDLE, MOVING_TO_TARGET, ATTACKING, USING_SKILL, RETREATING
}

enum class AIDifficulty { EASY, NORMAL, HARD }
