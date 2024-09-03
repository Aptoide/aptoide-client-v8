package com.aptoide.android.aptoidegames.installer

import android.graphics.drawable.Drawable

data class AppDetails(
  val appId: Long?,
  val name: String,
  val iconUrl: String?,
  val icon: Drawable?,
)
