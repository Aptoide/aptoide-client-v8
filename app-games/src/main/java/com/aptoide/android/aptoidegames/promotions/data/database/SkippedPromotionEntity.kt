package com.aptoide.android.aptoidegames.promotions.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SkippedPromotionEntities")
data class SkippedPromotionEntity(
  @PrimaryKey val packageName: String,
  val skippedAt: Long,
)
