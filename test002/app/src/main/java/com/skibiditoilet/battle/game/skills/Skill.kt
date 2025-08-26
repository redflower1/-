package com.skibiditoilet.battle.game.skills

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.skibiditoilet.battle.game.characters.Character
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 技能基类
 */
abstract class Skill(
    val id: String,
    val name: String,
    val description: String,
    val manaCost: Float,
    val cooldownTime: Long, // 冷却时间（毫秒）
    val damage: Float,
    val range: Float,
    val skillType: SkillType,
    val iconColor: Color
) {
    private val _isOnCooldown = MutableStateFlow(false)
    val isOnCooldown: StateFlow<Boolean> = _isOnCooldown.asStateFlow()
    
    private val _cooldownProgress = MutableStateFlow(0f)
    val cooldownProgress: StateFlow<Float> = _cooldownProgress.asStateFlow()
    
    /**
     * 检查技能是否在冷却中
     */
    fun isOnCooldown(): Boolean = _isOnCooldown.value
    
    /**
     * 使用技能
     */
    suspend fun use(caster: Character, target: Offset? = null) {
        if (_isOnCooldown.value) return
        
        // 执行技能效果
        executeSkill(caster, target)
        
        // 开始冷却
        startCooldown()
    }
    
    /**
     * 执行技能具体效果（由子类实现）
     */
    abstract suspend fun executeSkill(caster: Character, target: Offset?)
    
    /**
     * 开始冷却计时
     */
    private suspend fun startCooldown() {
        _isOnCooldown.value = true
        _cooldownProgress.value = 1f
        
        val startTime = System.currentTimeMillis()
        val endTime = startTime + cooldownTime
        
        while (System.currentTimeMillis() < endTime) {
            val currentTime = System.currentTimeMillis()
            val remaining = endTime - currentTime
            _cooldownProgress.value = remaining.toFloat() / cooldownTime.toFloat()
            delay(50) // 更新频率
        }
        
        _isOnCooldown.value = false
        _cooldownProgress.value = 0f
    }
    
    /**
     * 重置冷却时间
     */
    fun resetCooldown() {
        _isOnCooldown.value = false
        _cooldownProgress.value = 0f
    }
    
    /**
     * 检查目标是否在技能范围内
     */
    fun isTargetInRange(casterPosition: Offset, target: Offset): Boolean {
        val dx = casterPosition.x - target.x
        val dy = casterPosition.y - target.y
        val distance = kotlin.math.sqrt(dx * dx + dy * dy)
        return distance <= range
    }
}

/**
 * 技能类型枚举
 */
enum class SkillType {
    ATTACK,     // 攻击技能
    DEFENSE,    // 防御技能
    HEAL,       // 治疗技能
    BUFF,       // 增益技能
    DEBUFF,     // 减益技能
    ULTIMATE    // 大招
}

/**
 * 攻击技能基类
 */
abstract class AttackSkill(
    id: String,
    name: String,
    description: String,
    manaCost: Float,
    cooldownTime: Long,
    damage: Float,
    range: Float,
    iconColor: Color,
    val areaOfEffect: Float = 0f // 范围伤害半径
) : Skill(id, name, description, manaCost, cooldownTime, damage, range, SkillType.ATTACK, iconColor)

/**
 * 防御技能基类
 */
abstract class DefenseSkill(
    id: String,
    name: String,
    description: String,
    manaCost: Float,
    cooldownTime: Long,
    range: Float,
    iconColor: Color,
    val shieldAmount: Float,
    val duration: Long // 持续时间（毫秒）
) : Skill(id, name, description, manaCost, cooldownTime, 0f, range, SkillType.DEFENSE, iconColor)

/**
 * 治疗技能基类
 */
abstract class HealSkill(
    id: String,
    name: String,
    description: String,
    manaCost: Float,
    cooldownTime: Long,
    range: Float,
    iconColor: Color,
    val healAmount: Float
) : Skill(id, name, description, manaCost, cooldownTime, 0f, range, SkillType.HEAL, iconColor)
