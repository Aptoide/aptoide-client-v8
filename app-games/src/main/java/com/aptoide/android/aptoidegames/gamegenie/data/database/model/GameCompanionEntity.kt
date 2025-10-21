package com.aptoide.android.aptoidegames.gamegenie.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GameCompanion")
data class GameCompanionEntity(
  @PrimaryKey val id: String,
  val name: String,
  val conversation: List<ChatInteractionEntity>,
  val gamePackageName: String,
  val lastMessageTimestamp: Long
)