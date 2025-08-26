package com.skibiditoilet.battle.game.characters

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.skibiditoilet.battle.game.skills.*
import com.skibiditoilet.battle.ui.theme.CameraGray

/**
 * 监控人角色
 * 特点：远程型角色，高机动性，精准攻击
 */
class CameraMan(id: String) : Character(
    id = id,
    name = "监控人",
    characterType = CharacterType.CAMERA_MAN,
    initialHealth = 800f,
    initialMana = 600f,
    maxHealth = 800f,
    maxMana = 600f,
    attackDamage = 150f,
    movementSpeed = 400f,
    attackRange = 500f
) {
    override val primaryColor: Color = CameraGray
    override val secondaryColor: Color = Color(0xFF708090)
    
    override val skills: List<Skill> = listOf(
        CameraFlash(),
        ZoomShot(),
        RecordingMode()
    )
    
    override val ultimateSkill: Skill = SurveillanceNetwork()
}

/**
 * 闪光灯攻击 - 技能1
 */
class CameraFlash : AttackSkill(
    id = "camera_flash",
    name = "闪光灯",
    description = "发出强烈闪光，致盲敌人并造成伤害",
    manaCost = 50f,
    cooldownTime = 6000L,
    damage = 120f,
    range = 350f,
    iconColor = Color(0xFFFFFFFF),
    areaOfEffect = 150f
) {
    override suspend fun executeSkill(caster: Character, target: Offset?) {
        // TODO: 实现闪光灯效果
        // 1. 播放闪光音效
        // 2. 显示强烈白光特效
        // 3. 对范围内敌人造成伤害和致盲效果（3秒内移动速度减半）
    }
}

/**
 * 变焦射击 - 技能2
 */
class ZoomShot : AttackSkill(
    id = "zoom_shot",
    name = "变焦射击",
    description = "精准瞄准远距离目标，造成高额伤害",
    manaCost = 80f,
    cooldownTime = 10000L,
    damage = 350f,
    range = 800f,
    iconColor = Color(0xFFFF4500),
    areaOfEffect = 0f
) {
    override suspend fun executeSkill(caster: Character, target: Offset?) {
        // TODO: 实现变焦射击效果
        // 1. 播放瞄准音效
        // 2. 显示瞄准线特效
        // 3. 1秒后发射激光，对单个目标造成高伤害
        // 4. 如果是暴击，伤害翻倍
    }
}

/**
 * 录制模式 - 技能3
 */
class RecordingMode : DefenseSkill(
    id = "recording_mode",
    name = "录制模式",
    description = "进入录制状态，提升移动速度和攻击速度",
    manaCost = 120f,
    cooldownTime = 25000L,
    range = 0f,
    iconColor = Color(0xFF00FF00),
    shieldAmount = 0f,
    duration = 10000L
) {
    override suspend fun executeSkill(caster: Character, target: Offset?) {
        // TODO: 实现录制模式效果
        // 1. 播放录制音效
        // 2. 显示录制指示灯特效
        // 3. 10秒内移动速度和攻击速度提升50%
        // 4. 期间所有技能冷却时间减半
    }
}

/**
 * 监控网络 - 大招
 */
class SurveillanceNetwork : AttackSkill(
    id = "surveillance_network",
    name = "监控网络",
    description = "建立监控网络，持续追踪并攻击所有敌人",
    manaCost = 250f,
    cooldownTime = 80000L,
    damage = 100f, // 每秒伤害
    range = 1000f,
    iconColor = Color(0xFF8A2BE2),
    areaOfEffect = 1000f
) {
    override suspend fun executeSkill(caster: Character, target: Offset?) {
        // TODO: 实现监控网络大招效果
        // 1. 播放网络连接音效
        // 2. 在地图上显示监控网络特效
        // 3. 持续8秒，每秒自动锁定并攻击视野内的所有敌人
        // 4. 被攻击的敌人会被标记，移动速度降低
    }
}
