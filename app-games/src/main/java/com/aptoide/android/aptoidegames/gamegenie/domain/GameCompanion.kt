package com.aptoide.android.aptoidegames.gamegenie.domain

import android.graphics.drawable.Drawable

data class GameCompanion(
  val name: String,
  val packageName: String,
  val versionName: String?,
  val image: Drawable?
)
