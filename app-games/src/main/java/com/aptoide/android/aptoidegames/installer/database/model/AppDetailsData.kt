package com.aptoide.android.aptoidegames.installer.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AppDetails")
data class AppDetailsData(
  @PrimaryKey val packageName: String,
  val appId: Long?,
  val name: String?,
  val icon: String?,
)
