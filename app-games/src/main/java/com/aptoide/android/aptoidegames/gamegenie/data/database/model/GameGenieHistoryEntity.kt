package com.aptoide.android.aptoidegames.gamegenie.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GameGenieHistory")
data class GameGenieHistoryEntity(
  @PrimaryKey val id: String,
  val title: String,
  val conversation: List<ChatInteractionEntity>,
)
