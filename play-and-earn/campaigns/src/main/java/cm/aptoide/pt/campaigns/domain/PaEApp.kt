package cm.aptoide.pt.campaigns.domain

import cm.aptoide.pt.extensions.getRandomString
import cm.aptoide.pt.feature_apps.data.emptyApp
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.domain.AppSource
import kotlin.random.Random
import kotlin.random.nextInt

data class PaEApp(
  override val packageName: String,
  val icon: String,
  val graphic: String,
  val name: String,
  val uname: String,
  val progress: PaEProgress?
) : AppSource

data class PaEProgress(
  val current: Int?,
  val target: Int,
  val type: String,
  val status: String?
) {
  fun getNormalizedProgress(): Float = current?.toFloat()?.div(target)?.coerceIn(0f, 1f) ?: 0f
}

fun PaEApp.asNormalApp() = emptyApp.copy(
  packageName = packageName,
  icon = icon,
  featureGraphic = graphic,
  name = name,
  isAppCoins = true
)

val randomPaEApp
  get() = randomApp.let {
    PaEApp(
      packageName = it.packageName,
      icon = it.icon,
      graphic = it.featureGraphic,
      name = it.name,
      uname = getRandomString(range = 1..3, separator = "-"),
      progress = randomPaEProgress
    )
  }

val randomPaEProgress
  get() = Random.nextInt(20..100).let {
    PaEProgress(
      current = Random.nextInt(0..it),
      target = it,
      type = "",
      status = null
    )
  }