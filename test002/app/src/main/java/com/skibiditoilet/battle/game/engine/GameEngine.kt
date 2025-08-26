package com.skibiditoilet.battle.game.engine

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.skibiditoilet.battle.game.characters.Character
import kotlin.math.*

/**
 * 单机游戏引擎
 * 专注于人机对战的核心逻辑
 */
class GameEngine {

    // 游戏世界大小
    val worldSize = Size(1000f, 800f)

    // 伤害计算系统
    private val damageSystem = DamageSystem()

    /**
     * 检查两个角色是否在攻击范围内
     */
    fun isInRange(attacker: Character, target: Character, range: Float): Boolean {
        val distance = getDistance(
            attacker.position.value,
            target.position.value
        )
        return distance <= range
    }

    /**
     * 计算两点之间的距离
     */
    fun getDistance(pos1: Offset, pos2: Offset): Float {
        val dx = pos1.x - pos2.x
        val dy = pos1.y - pos2.y
        return sqrt(dx * dx + dy * dy)
    }

    /**
     * 执行攻击
     */
    fun performAttack(attacker: Character, target: Character): AttackResult {
        if (!isInRange(attacker, target, attacker.attackRange)) {
            return AttackResult.OUT_OF_RANGE
        }

        if (!attacker.isAlive.value || !target.isAlive.value) {
            return AttackResult.INVALID_TARGET
        }

        val damage = damageSystem.calculateDamage(attacker.attackDamage, attacker, target)
        val actualDamage = target.takeDamage(damage)

        return if (actualDamage > 0) {
            if (!target.isAlive.value) {
                AttackResult.KILL
            } else {
                AttackResult.HIT
            }
        } else {
            AttackResult.MISS
        }
    }

    /**
     * 检查位置是否在世界边界内
     */
    fun isValidPosition(position: Offset): Boolean {
        return position.x >= 0 && position.x <= worldSize.width &&
               position.y >= 0 && position.y <= worldSize.height
    }

    /**
     * 限制位置在世界边界内
     */
    fun clampToWorld(position: Offset): Offset {
        return Offset(
            position.x.coerceIn(50f, worldSize.width - 50f),
            position.y.coerceIn(50f, worldSize.height - 50f)
        )
    }

    /**
     * 寻找最近的敌人
     */
    fun findNearestEnemy(character: Character, enemies: List<Character>): Character? {
        return enemies
            .filter { it.isAlive.value && it != character }
            .minByOrNull { getDistance(character.position.value, it.position.value) }
    }

    /**
     * 计算移动方向
     */
    fun calculateMoveDirection(from: Offset, to: Offset): Offset {
        val dx = to.x - from.x
        val dy = to.y - from.y
        val distance = sqrt(dx * dx + dy * dy)

        return if (distance > 0) {
            Offset(dx / distance, dy / distance)
        } else {
            Offset.Zero
        }
    }
}

/**
 * 伤害计算系统
 */
class DamageSystem {

    fun calculateDamage(
        baseDamage: Float,
        attacker: Character,
        target: Character
    ): Float {
        // 基础伤害
        var damage = baseDamage

        // 添加随机波动 (85% - 115%)
        val randomFactor = 0.85f + (Math.random().toFloat() * 0.3f)
        damage *= randomFactor

        // 暴击判断 (10%概率)
        if (Math.random() < 0.1) {
            damage *= 2.0f
        }

        return damage.coerceAtLeast(1f)
    }
}

/**
 * 攻击结果枚举
 */
enum class AttackResult {
    HIT,            // 命中
    MISS,           // 未命中
    KILL,           // 击杀
    OUT_OF_RANGE,   // 超出范围
    INVALID_TARGET  // 无效目标
}
