package cm.aptoide.pt.feature_apps.data

import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import cm.aptoide.pt.extensions.getRandomString
import kotlin.random.Random

data class MyGamesApp(
  val icon: Drawable,
  val name: String,
  val packageName: String,
  val versionName: String?,
)

val randomMyGamesApp
  get() = MyGamesApp(
    icon = VectorDrawable(),
    name = getRandomString(range = 2..5, capitalize = true),
    packageName = getRandomString(range = 3..5, separator = "."),
    versionName = if (Random.nextBoolean()) {
      "${Random.nextInt(3)}.${Random.nextInt(20)}.${Random.nextInt(100)}"
    } else {
      null
    }
  )
