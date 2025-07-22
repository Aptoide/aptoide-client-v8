package com.aptoide.android.aptoidegames.play_and_earn.presentation.level_up

import androidx.compose.ui.graphics.Color
import com.aptoide.android.aptoidegames.theme.Palette

sealed interface LevelProperties {
  val level: Int
  val name: String
  val mainColor: Color
  val isPlusVariant: Boolean

  companion object {
    fun fromLevel(level: Int): LevelProperties {
      return when (level) {
        0 -> LevelBronze
        1 -> LevelBronzePlus
        2 -> LevelSilver
        3 -> LevelSilverPlus
        4 -> LevelGold
        5 -> LevelGoldPlus
        6 -> LevelPlatinum
        7 -> LevelPlatinumPlus
        8 -> LevelVip
        9 -> LevelVipPlus
        else -> throw IllegalArgumentException("Invalid level: $level")
      }
    }
  }
}

fun LevelProperties.isVIP() = this.level == 8 || this.level == 9

object LevelBronze : LevelProperties {
  override val level = 0
  override val name = "Bronze"
  override val mainColor = Palette.Orange150
  override val isPlusVariant = false
}

object LevelBronzePlus : LevelProperties {
  override val level = 1
  override val name = "Bronze"
  override val mainColor = Palette.Orange150
  override val isPlusVariant = true
}

object LevelSilver : LevelProperties {
  override val level = 2
  override val name = "Silver"
  override val mainColor = Palette.Blue100
  override val isPlusVariant = false
}

object LevelSilverPlus : LevelProperties {
  override val level = 3
  override val name = "Silver"
  override val mainColor = Palette.Blue100
  override val isPlusVariant = true
}

object LevelGold : LevelProperties {
  override val level = 4
  override val name = "Gold"
  override val mainColor = Palette.Yellow100
  override val isPlusVariant = false
}

object LevelGoldPlus : LevelProperties {
  override val level = 5
  override val name = "Gold"
  override val mainColor = Palette.Yellow100
  override val isPlusVariant = true
}

object LevelPlatinum : LevelProperties {
  override val level = 6
  override val name = "Platinum"
  override val mainColor = Palette.Blue150
  override val isPlusVariant = false
}

object LevelPlatinumPlus : LevelProperties {
  override val level = 7
  override val name = "Platinum"
  override val mainColor = Palette.Blue150
  override val isPlusVariant = true
}

object LevelVip : LevelProperties {
  override val level = 8
  override val name = "VIP"
  override val mainColor = Palette.Yellow100
  override val isPlusVariant = false
}

object LevelVipPlus : LevelProperties {
  override val level = 9
  override val name = "VIP"
  override val mainColor = Palette.Yellow100
  override val isPlusVariant = true
}
