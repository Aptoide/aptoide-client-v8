package com.aptoide.android.aptoidegames.play_and_earn.domain

data class Level(
  val level: Int,
  val amount: Long,
  val bonus: Float,
)

val levels = listOf(
  Level(
    level = 0,
    amount = 0,
    bonus = 5f,
  ),
  Level(
    level = 1,
    amount = 2000,
    bonus = 6f,
  ),
  Level(
    level = 2,
    amount = 10000,
    bonus = 7f,
  ),
  Level(
    level = 3,
    amount = 50000,
    bonus = 8.5f,
  ),
  Level(
    level = 4,
    amount = 100000,
    bonus = 10f,
  ),
  Level(
    level = 5,
    amount = 250000,
    bonus = 12f,
  ),
  Level(
    level = 6,
    amount = 750000,
    bonus = 14f,
  ),
  Level(
    level = 7,
    amount = 1500000,
    bonus = 16f,
  ),
  Level(
    level = 8,
    amount = 2500000,
    bonus = 18f,
  ),
  Level(
    level = 9,
    amount = 5000000,
    bonus = 20f,
  )
)
