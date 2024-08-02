package cm.aptoide.pt.feature_updates.domain

import android.graphics.drawable.Drawable

data class InstalledApp(
  val appName: String,
  val packageName: String,
  val versionName: String,
  val versionCode: Int,
  val appIcon: Drawable,
)
