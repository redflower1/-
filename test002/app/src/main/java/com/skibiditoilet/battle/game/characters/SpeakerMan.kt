package com.skibiditoilet.battle.game.characters

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.skibiditoilet.battle.game.skills.*
import com.skibiditoilet.battle.ui.theme.SpeakerBlue

/**
 * 音响人角色
 * 特点：支援型角色，范围控制，团队增益
 */
class SpeakerMan(id: String) : Character(
    id = id,
    name = "音响人",
    characterType = CharacterType.SPEAKER_MAN,
    initialHealth = 900f,
    initialMana = 700f,
    maxHealth = 900f,
    maxMana = 700f,
    attackDamage = 120f,
    movementSpeed = 350f,
    attackRange = 400f
) {
    override val primaryColor: Color = SpeakerBlue
    override val secondaryColor: Color = Color(0xFF87CEEB)
    
    override val skills: List<Skill> = listOf(
        SonicWave(),
        VolumeBoost(),
        SoundBarrier()
    )
    
    override val ultimateSkill: Skill = BassDropUltimate()
}

/**
 * 声波攻击 - 技能1
 */
class SonicWave : AttackSkill(
    id = "sonic_wave",
    name = "声波攻击",
    description = "发射声波，对直线上的敌人造成伤害",
    manaCost = 70f,
    cooldownTime = 8000L,
    damage = 160f,
    range = 600f,
    iconColor = Color(0xFF00BFFF),
    areaOfEffect = 80f
) {
    override suspend fun executeSkill(caster: Character, target: Offset?) {
        // TODO: 实现声波攻击效果
        // 1. 播放声波音效
        // 2. 显示声波扩散特效
        // 3. 对直线路径上的所有敌人造成伤害
    }
}

/**
 * 音量增强 - 技能2
 */
class VolumeBoost : Skill(
    id = "volume_boost",
    name = "音量增强",
    description = "为队友提供攻击力和移动速度加成",
    manaCost = 100f,
    cooldownTime = 15000L,
    damage = 0f,
    range = 500f,
    skillType = SkillType.BUFF,
    iconColor = Color(0xFFFFD700)
) {
    override suspend fun executeSkill(caster: Character, target: Offset?) {
        // TODO: 实现音量增强效果
        // 1. 播放增益音效
        // 2. 显示音符特效
        // 3. 为范围内队友提供15秒的攻击力+30%，移动速度+20%
    }
}

/**
 * 声音屏障 - 技能3
 */
class SoundBarrier : DefenseSkill(
    id = "sound_barrier",
    name = "声音屏障",
    description = "创建声音屏障，阻挡敌人攻击",
    manaCost = 120f,
    cooldownTime = 20000L,
    range = 300f,
    iconColor = Color(0xFF9370DB),
    shieldAmount = 500f,
    duration = 12000L
) {
    override suspend fun executeSkill(caster: Character, target: Offset?) {
        // TODO: 实现声音屏障效果
        // 1. 播放屏障音效
        // 2. 显示声波屏障特效
        // 3. 在指定位置创建屏障，可以阻挡敌人的远程攻击
    }
}

/**
 * 低音炸弹 - 大招
 */
class BassDropUltimate : AttackSkill(
    id = "bass_drop",
    name = "低音炸弹",
    description = "释放强力低音波，对全场敌人造成巨大伤害",
    manaCost = 300f,
    cooldownTime = 90000L,
    damage = 400f,
    range = 800f,
    iconColor = Color(0xFF8B008B),
    areaOfEffect = 800f
) {
    override suspend fun executeSkill(caster: Character, target: Offset?) {
        // TODO: 实现低音炸弹大招效果
        // 1. 播放低音炸弹音效
        // 2. 屏幕震动效果
        // 3. 对全场敌人造成巨大伤害
        // 4. 被击中的敌人会被眩晕2秒
    }
}
