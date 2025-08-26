package com.skibiditoilet.battle.game.characters

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.skibiditoilet.battle.game.skills.*
import com.skibiditoilet.battle.ui.theme.ToiletBrown

/**
 * 马桶人角色
 * 特点：近战型角色，高血量，强攻击力
 */
class ToiletMan(id: String) : Character(
    id = id,
    name = "马桶人",
    characterType = CharacterType.TOILET_MAN,
    initialHealth = 1200f,
    initialMana = 400f,
    maxHealth = 1200f,
    maxMana = 400f,
    attackDamage = 180f,
    movementSpeed = 300f,
    attackRange = 150f
) {
    override val primaryColor: Color = ToiletBrown
    override val secondaryColor: Color = Color(0xFFCD853F)
    
    override val skills: List<Skill> = listOf(
        ToiletFlushAttack(),
        ToiletSlam(),
        ToiletShield()
    )
    
    override val ultimateSkill: Skill = ToiletTornado()
}

/**
 * 马桶冲水攻击 - 技能1
 */
class ToiletFlushAttack : AttackSkill(
    id = "toilet_flush",
    name = "冲水攻击",
    description = "向前方发射强力水流，造成伤害并击退敌人",
    manaCost = 60f,
    cooldownTime = 8000L,
    damage = 220f,
    range = 400f,
    iconColor = Color(0xFF4169E1),
    areaOfEffect = 100f
) {
    override suspend fun executeSkill(caster: Character, target: Offset?) {
        // TODO: 实现冲水攻击效果
        // 1. 播放冲水音效
        // 2. 显示水流特效
        // 3. 对范围内敌人造成伤害和击退效果
    }
}

/**
 * 马桶重击 - 技能2
 */
class ToiletSlam : AttackSkill(
    id = "toilet_slam",
    name = "马桶重击",
    description = "用力撞击地面，对周围敌人造成范围伤害",
    manaCost = 80f,
    cooldownTime = 12000L,
    damage = 280f,
    range = 200f,
    iconColor = Color(0xFF8B4513),
    areaOfEffect = 200f
) {
    override suspend fun executeSkill(caster: Character, target: Offset?) {
        // TODO: 实现重击效果
        // 1. 播放撞击音效
        // 2. 显示地面震动特效
        // 3. 对周围敌人造成范围伤害
    }
}

/**
 * 马桶护盾 - 技能3
 */
class ToiletShield : DefenseSkill(
    id = "toilet_shield",
    name = "马桶护盾",
    description = "生成保护盾，减少受到的伤害",
    manaCost = 100f,
    cooldownTime = 20000L,
    range = 0f,
    iconColor = Color(0xFF32CD32),
    shieldAmount = 400f,
    duration = 8000L
) {
    override suspend fun executeSkill(caster: Character, target: Offset?) {
        // TODO: 实现护盾效果
        // 1. 播放护盾音效
        // 2. 显示护盾特效
        // 3. 为角色添加护盾状态
    }
}

/**
 * 马桶龙卷风 - 大招
 */
class ToiletTornado : AttackSkill(
    id = "toilet_tornado",
    name = "马桶龙卷风",
    description = "召唤强力龙卷风，对大范围敌人造成持续伤害",
    manaCost = 200f,
    cooldownTime = 60000L,
    damage = 150f, // 每秒伤害
    range = 600f,
    iconColor = Color(0xFF9370DB),
    areaOfEffect = 300f
) {
    override suspend fun executeSkill(caster: Character, target: Offset?) {
        // TODO: 实现龙卷风大招效果
        // 1. 播放龙卷风音效
        // 2. 显示龙卷风特效动画
        // 3. 持续5秒，每秒对范围内敌人造成伤害
        // 4. 龙卷风会缓慢移动
    }
}
