package cm.aptoide.pt.feature_home.domain

import cm.aptoide.pt.feature_apps.data.App

open class Bundle(
  val title: String,
  val appsListList: List<List<App>> = emptyList(),
  val type: Type,
  val tag: String,
  val bundleIcon: String? = null,
  val graphic: String? = null,
  val background: String? = null,
  val bundleAction: WidgetActionEventName? = null,
  val view: String? = null
) {
  val appsList get() = appsListList[0]
}

enum class Type {
  FEATURE_GRAPHIC,
  APP_GRID,
  ESKILLS,
  FEATURED_APPC,
  EDITORIAL,
  UNKNOWN_BUNDLE,
  MY_APPS,
  CAROUSEL,
  CAROUSEL_LARGE,
  LIST,
  PUBLISHER_TAKEOVER
}