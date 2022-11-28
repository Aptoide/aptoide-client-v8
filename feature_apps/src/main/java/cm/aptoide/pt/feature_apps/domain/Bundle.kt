package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App

open class Bundle(
  val title: String,
  val appsList: List<App>,
  val type: Type,
  val bundleIcon: String? = null,
  val graphic: String? = null,
  val bundleAction: BundleAction? = null,
  val view: String? = null
)

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
  LIST
}