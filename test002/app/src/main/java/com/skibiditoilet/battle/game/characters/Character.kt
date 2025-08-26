package com.skibiditoilet.battle.game.characters

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.skibiditoilet.battle.game.skills.Skill
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 游戏角色基类
 */
abstract class Character(
    val id: String,
    val name: String,
    val characterType: CharacterType,
    initialHealth: Float,
    initialMana: Float,
    val maxHealth: Float,
    val maxMana: Float,
    val attackDamage: Float,
    val movementSpeed: Float,
    val attackRange: Float
) {
    // 角色状态
    private val _health = MutableStateFlow(initialHealth)
    val health: StateFlow<Float> = _health.asStateFlow()
    
    private val _mana = MutableStateFlow(initialMana)
    val mana: StateFlow<Float> = _mana.asStateFlow()
    
    private val _position = MutableStateFlow(Offset.Zero)
    val position: StateFlow<Offset> = _position.asStateFlow()
    
    private val _isAlive = MutableStateFlow(true)
    val isAlive: StateFlow<Boolean> = _isAlive.asStateFlow()
    
    private val _isMoving = MutableStateFlow(false)
    val isMoving: StateFlow<Boolean> = _isMoving.asStateFlow()
    
    private val _facingDirection = MutableStateFlow(Direction.RIGHT)
    val facingDirection: StateFlow<Direction> = _facingDirection.asStateFlow()
    
    // 技能系统
    abstract val skills: List<Skill>
    abstract val ultimateSkill: Skill
    
    // 角色颜色主题
    abstract val primaryColor: Color
    abstract val secondaryColor: Color
    
    /**
     * 移动角色到指定位置
     */
    fun moveTo(newPosition: Offset) {
        if (_isAlive.value) {
            _position.value = newPosition
            updateFacingDirection(newPosition)
        }
    }
    
    /**
     * 更新面向方向
     */
    private fun updateFacingDirection(newPosition: Offset) {
        val currentPos = _position.value
        if (newPosition.x > currentPos.x) {
            _facingDirection.value = Direction.RIGHT
        } else if (newPosition.x < currentPos.x) {
            _facingDirection.value = Direction.LEFT
        }
    }
    
    /**
     * 设置移动状态
     */
    fun setMoving(moving: Boolean) {
        _isMoving.value = moving
    }
    
    /**
     * 受到伤害
     */
    fun takeDamage(damage: Float): Float {
        if (!_isAlive.value) return 0f
        
        val actualDamage = damage.coerceAtMost(_health.value)
        _health.value = (_health.value - actualDamage).coerceAtLeast(0f)
        
        if (_health.value <= 0f) {
            _isAlive.value = false
        }
        
        return actualDamage
    }
    
    /**
     * 恢复生命值
     */
    fun heal(amount: Float) {
        if (_isAlive.value) {
            _health.value = (_health.value + amount).coerceAtMost(maxHealth)
        }
    }
    
    /**
     * 消耗法力值
     */
    fun consumeMana(amount: Float): Boolean {
        if (_mana.value >= amount) {
            _mana.value = (_mana.value - amount).coerceAtLeast(0f)
            return true
        }
        return false
    }
    
    /**
     * 恢复法力值
     */
    fun restoreMana(amount: Float) {
        _mana.value = (_mana.value + amount).coerceAtMost(maxMana)
    }
    
    /**
     * 检查是否可以使用技能
     */
    fun canUseSkill(skill: Skill): Boolean {
        return _isAlive.value && 
               _mana.value >= skill.manaCost && 
               !skill.isOnCooldown()
    }
    
    /**
     * 使用技能
     */
    suspend fun useSkill(skill: Skill, target: Offset? = null): Boolean {
        if (!canUseSkill(skill)) return false
        
        consumeMana(skill.manaCost)
        skill.use(this, target)
        return true
    }
    
    /**
     * 重置角色状态（用于游戏重新开始）
     */
    fun reset() {
        _health.value = maxHealth
        _mana.value = maxMana
        _position.value = Offset.Zero
        _isAlive.value = true
        _isMoving.value = false
        _facingDirection.value = Direction.RIGHT
        
        skills.forEach { it.resetCooldown() }
        ultimateSkill.resetCooldown()
    }
    
    /**
     * 获取角色与目标的距离
     */
    fun getDistanceTo(target: Offset): Float {
        val dx = _position.value.x - target.x
        val dy = _position.value.y - target.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
    
    /**
     * 检查目标是否在攻击范围内
     */
    fun isInAttackRange(target: Offset): Boolean {
        return getDistanceTo(target) <= attackRange
    }
}

/**
 * 角色类型枚举
 */
enum class CharacterType {
    TOILET_MAN,     // 马桶人
    CAMERA_MAN,     // 监控人
    SPEAKER_MAN,    // 音响人
    TV_MAN          // 电视人
}

/**
 * 方向枚举
 */
enum class Direction {
    LEFT, RIGHT, UP, DOWN
}
