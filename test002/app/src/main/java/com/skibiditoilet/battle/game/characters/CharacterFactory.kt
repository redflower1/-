package com.skibiditoilet.battle.game.characters

import java.util.UUID

/**
 * 角色工厂类
 * 负责创建和管理游戏角色
 */
object CharacterFactory {
    
    /**
     * 根据角色类型创建角色
     */
    fun createCharacter(type: CharacterType, customId: String? = null): Character {
        val id = customId ?: UUID.randomUUID().toString()
        
        return when (type) {
            CharacterType.TOILET_MAN -> ToiletMan(id)
            CharacterType.CAMERA_MAN -> CameraMan(id)
            CharacterType.SPEAKER_MAN -> SpeakerMan(id)
            CharacterType.TV_MAN -> TVMan(id)
        }
    }
    
    /**
     * 获取所有可用的角色类型
     */
    fun getAllCharacterTypes(): List<CharacterType> {
        return CharacterType.values().toList()
    }
    
    /**
     * 获取角色类型的显示名称
     */
    fun getCharacterDisplayName(type: CharacterType): String {
        return when (type) {
            CharacterType.TOILET_MAN -> "马桶人"
            CharacterType.CAMERA_MAN -> "监控人"
            CharacterType.SPEAKER_MAN -> "音响人"
            CharacterType.TV_MAN -> "电视人"
        }
    }
    
    /**
     * 获取角色类型的描述
     */
    fun getCharacterDescription(type: CharacterType): String {
        return when (type) {
            CharacterType.TOILET_MAN -> "近战坦克型角色，拥有高血量和强大的近战攻击能力，擅长冲锋陷阵"
            CharacterType.CAMERA_MAN -> "远程射手型角色，拥有精准的远程攻击和高机动性，擅长风筝战术"
            CharacterType.SPEAKER_MAN -> "支援辅助型角色，能够为队友提供增益效果，擅长团队作战"
            CharacterType.TV_MAN -> "法师爆发型角色，拥有强大的范围伤害技能，擅长团战输出"
        }
    }
    
    /**
     * 获取角色的推荐难度
     */
    fun getCharacterDifficulty(type: CharacterType): CharacterDifficulty {
        return when (type) {
            CharacterType.TOILET_MAN -> CharacterDifficulty.EASY
            CharacterType.CAMERA_MAN -> CharacterDifficulty.MEDIUM
            CharacterType.SPEAKER_MAN -> CharacterDifficulty.HARD
            CharacterType.TV_MAN -> CharacterDifficulty.MEDIUM
        }
    }
    
    /**
     * 获取角色的角色定位
     */
    fun getCharacterRole(type: CharacterType): CharacterRole {
        return when (type) {
            CharacterType.TOILET_MAN -> CharacterRole.TANK
            CharacterType.CAMERA_MAN -> CharacterRole.MARKSMAN
            CharacterType.SPEAKER_MAN -> CharacterRole.SUPPORT
            CharacterType.TV_MAN -> CharacterRole.MAGE
        }
    }
    
    /**
     * 创建默认的角色组合（用于AI或测试）
     */
    fun createDefaultTeam(): List<Character> {
        return listOf(
            createCharacter(CharacterType.TOILET_MAN),
            createCharacter(CharacterType.CAMERA_MAN),
            createCharacter(CharacterType.SPEAKER_MAN),
            createCharacter(CharacterType.TV_MAN)
        )
    }
    
    /**
     * 创建平衡的对战队伍
     */
    fun createBalancedTeam(teamSize: Int = 2): List<Character> {
        val types = when (teamSize) {
            1 -> listOf(CharacterType.TOILET_MAN)
            2 -> listOf(CharacterType.TOILET_MAN, CharacterType.CAMERA_MAN)
            3 -> listOf(CharacterType.TOILET_MAN, CharacterType.CAMERA_MAN, CharacterType.SPEAKER_MAN)
            else -> listOf(CharacterType.TOILET_MAN, CharacterType.CAMERA_MAN, CharacterType.SPEAKER_MAN, CharacterType.TV_MAN)
        }
        
        return types.map { createCharacter(it) }
    }
}

/**
 * 角色难度枚举
 */
enum class CharacterDifficulty {
    EASY,       // 简单
    MEDIUM,     // 中等
    HARD        // 困难
}

/**
 * 角色定位枚举
 */
enum class CharacterRole {
    TANK,       // 坦克
    MARKSMAN,   // 射手
    MAGE,       // 法师
    SUPPORT,    // 辅助
    ASSASSIN    // 刺客
}
