package cm.aptoide.pt.feature_home.domain

import cm.aptoide.pt.feature_apps.data.App

open class Bundle(
  val title: String,
  val appsListList: List<List<App>> = emptyList(),
  val type: Type,
  val tag: String,
  val bundleIcon: String? = null,
  val background: String? = null,
  val hasMoreAction: Boolean = false,
  val view: String? = null,
  val bundleSource: BundleSource = BundleSource.MANUAL,
  val timestamp: String = System.currentTimeMillis().toString()
) {
  val appsList get() = appsListList.getOrNull(0) ?: emptyList()
}

enum class BundleSource {
  AUTOMATIC, MANUAL
}


enum class Type {
  FEATURE_GRAPHIC,
  APP_GRID,
  ESKILLS,
  FEATURED_APPC,
  EDITORIAL,
  UNKNOWN_BUNDLE,
  MY_GAMES,
  CAROUSEL,
  CAROUSEL_LARGE,
  LIST,
  PUBLISHER_TAKEOVER,
  CATEGORIES,
  HTML_GAMES
}