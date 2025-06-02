package cm.aptoide.pt.feature_updates.domain

import cm.aptoide.pt.extensions.ifNormalAppOrGame
import cm.aptoide.pt.install_manager.App

fun Collection<App>.filterNormalAppsOrGames(): List<App> {
  return this.filter { it.packageInfo?.ifNormalAppOrGame() ?: false }
}
