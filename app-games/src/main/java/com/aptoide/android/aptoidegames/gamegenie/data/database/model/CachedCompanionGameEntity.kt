package com.aptoide.android.aptoidegames.gamegenie.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CachedCompanionGame")
data class CachedCompanionGameEntity(
  @PrimaryKey val packageName: String,
  val name: String,
  val versionName: String?,
  val cachedAtMs: Long,
)
