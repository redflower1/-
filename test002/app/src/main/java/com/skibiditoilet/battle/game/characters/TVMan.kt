package com.skibiditoilet.battle.game.characters

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.skibiditoilet.battle.game.skills.*
import com.skibiditoilet.battle.ui.theme.TVBlack

/**
 * 电视人角色
 * 特点：法师型角色，高爆发，范围伤害
 */
class TVMan(id: String) : Character(
    id = id,
    name = "电视人",
    characterType = CharacterType.TV_MAN,
    initialHealth = 700f,
    initialMana = 800f,
    maxHealth = 700f,
    maxMana = 800f,
    attackDamage = 140f,
    movementSpeed = 320f,
    attackRange = 450f
) {
    override val primaryColor: Color = TVBlack
    override val secondaryColor: Color = Color(0xFF2F2F2F)
    
    override val skills: List<Skill> = listOf(
        StaticShock(),
        ScreenFlicker(),
        SignalJam()
    )
    
    override val ultimateSkill: Skill = BroadcastOverload()
}

/**
 * 静电冲击 - 技能1
 */
class StaticShock : AttackSkill(
    id = "static_shock",
    name = "静电冲击",
    description = "释放静电，对敌人造成伤害并降低其移动速度",
    manaCost = 60f,
    cooldownTime = 7000L,
    damage = 180f,
    range = 400f,
    iconColor = Color(0xFFFFFF00),
    areaOfEffect = 120f
) {
    override suspend fun executeSkill(caster: Character, target: Offset?) {
        // TODO: 实现静电冲击效果
        // 1. 播放电击音效
        // 2. 显示闪电特效
        // 3. 对目标造成伤害并减速3秒
    }
}

/**
 * 屏幕闪烁 - 技能2
 */
class ScreenFlicker : AttackSkill(
    id = "screen_flicker",
    name = "屏幕闪烁",
    description = "快速闪烁屏幕，干扰敌人视线并造成持续伤害",
    manaCost = 90f,
    cooldownTime = 12000L,
    damage = 80f, // 每秒伤害
    range = 500f,
    iconColor = Color(0xFF00FF00),
    areaOfEffect = 200f
) {
    override suspend fun executeSkill(caster: Character, target: Offset?) {
        // TODO: 实现屏幕闪烁效果
        // 1. 播放闪烁音效
        // 2. 屏幕闪烁特效
        // 3. 持续5秒，每秒对范围内敌人造成伤害
        // 4. 被影响的敌人准确度降低
    }
}

/**
 * 信号干扰 - 技能3
 */
class SignalJam : Skill(
    id = "signal_jam",
    name = "信号干扰",
    description = "干扰敌人信号，使其无法使用技能",
    manaCost = 150f,
    cooldownTime = 25000L,
    damage = 0f,
    range = 600f,
    skillType = SkillType.DEBUFF,
    iconColor = Color(0xFFFF0000)
) {
    override suspend fun executeSkill(caster: Character, target: Offset?) {
        // TODO: 实现信号干扰效果
        // 1. 播放干扰音效
        // 2. 显示信号波纹特效
        // 3. 8秒内目标无法使用技能
        // 4. 被干扰的敌人移动会有延迟
    }
}

/**
 * 广播过载 - 大招
 */
class BroadcastOverload : AttackSkill(
    id = "broadcast_overload",
    name = "广播过载",
    description = "过载所有电子设备，对全场造成巨大伤害",
    manaCost = 350f,
    cooldownTime = 100000L,
    damage = 500f,
    range = 1000f,
    iconColor = Color(0xFFFF1493),
    areaOfEffect = 1000f
) {
    override suspend fun executeSkill(caster: Character, target: Offset?) {
        // TODO: 实现广播过载大招效果
        // 1. 播放过载音效
        // 2. 全屏闪烁特效
        // 3. 对全场敌人造成巨大伤害
        // 4. 所有敌人的技能冷却时间增加50%
        // 5. 持续10秒的视觉干扰效果
    }
}
