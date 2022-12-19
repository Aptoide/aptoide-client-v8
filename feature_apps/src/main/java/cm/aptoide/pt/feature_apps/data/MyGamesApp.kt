package cm.aptoide.pt.feature_apps.data

import android.graphics.drawable.Drawable

data class MyGamesApp(
  val icon: Drawable,
  val name: String,
  val packageName: String,
  val versionName: String?
)
