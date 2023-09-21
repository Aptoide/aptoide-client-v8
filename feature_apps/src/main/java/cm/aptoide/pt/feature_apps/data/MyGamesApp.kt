package cm.aptoide.pt.feature_apps.data

import cm.aptoide.pt.extensions.getRandomString
import kotlin.random.Random

data class MyGamesApp(
  val name: String,
  val packageName: String,
  val versionName: String?,
)

val randomMyGamesApp
  get() = MyGamesApp(
    name = getRandomString(range = 2..5, capitalize = true),
    packageName = getRandomString(range = 3..5, separator = "."),
    versionName = if (Random.nextBoolean()) {
      "${Random.nextInt(3)}.${Random.nextInt(20)}.${Random.nextInt(100)}"
    } else {
      null
    }
  )
