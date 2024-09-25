package com.aptoide.android.aptoidegames.installer

import android.graphics.drawable.Drawable
import cm.aptoide.pt.feature_apps.domain.AppSource

data class AppDetails(
  override val appId: Long?,
  override val packageName: String,
  val name: String,
  val iconUrl: String?,
  val icon: Drawable?,
): AppSource
