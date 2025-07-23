package com.aptoide.android.aptoidegames.play_and_earn.domain

data class Level(
  val xp: Int,
  val level: Int,
  val tier: Tier,
  val bonus: Float,
  val status: LevelCheckpointStatus
)

enum class Tier {
  ZERO,
  BRONZE,
  BRONZE_PLUS,
  SILVER,
  SILVER_PLUS,
  GOLD,
  GOLD_PLUS,
  PLATINUM,
  PLATINUM_PLUS,
  VIP,
  VIP_PLUS
}

enum class LevelCheckpointStatus {
  UNLOCKED,
  CURRENT,
  LOCKED
}

val levels = listOf(
  Level(
    xp = 0,
    level = 0,
    tier = Tier.ZERO,
    bonus = 0f,
    status = LevelCheckpointStatus.UNLOCKED
  ),
  Level(
    xp = 500,
    level = 1,
    tier = Tier.BRONZE,
    bonus = 5f,
    status = LevelCheckpointStatus.UNLOCKED
  ),
  Level(
    xp = 1000,
    level = 2,
    tier = Tier.BRONZE_PLUS,
    bonus = 6f,
    status = LevelCheckpointStatus.CURRENT
  ),
  Level(
    xp = 2000,
    level = 3,
    tier = Tier.SILVER,
    bonus = 7f,
    status = LevelCheckpointStatus.LOCKED
  ),
  Level(
    xp = 3000,
    level = 4,
    tier = Tier.SILVER_PLUS,
    bonus = 8.5f,
    status = LevelCheckpointStatus.LOCKED
  ),
  Level(
    xp = 4500,
    level = 5,
    tier = Tier.GOLD,
    bonus = 10f,
    status = LevelCheckpointStatus.LOCKED
  ),
  Level(
    xp = 6750,
    level = 6,
    tier = Tier.GOLD_PLUS,
    bonus = 12f,
    status = LevelCheckpointStatus.LOCKED
  ),
  Level(
    xp = 10125,
    level = 7,
    tier = Tier.PLATINUM,
    bonus = 14f,
    status = LevelCheckpointStatus.LOCKED
  ),
  Level(
    xp = 15250,
    level = 8,
    tier = Tier.PLATINUM_PLUS,
    bonus = 16f,
    status = LevelCheckpointStatus.LOCKED
  ),
  Level(
    xp = 22750,
    level = 9,
    tier = Tier.VIP,
    bonus = 18f,
    status = LevelCheckpointStatus.LOCKED
  ),
  Level(
    xp = 22750,
    level = 10,
    tier = Tier.VIP_PLUS,
    bonus = 20f,
    status = LevelCheckpointStatus.LOCKED
  )
)
