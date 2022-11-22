package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.MyAppsApp

open class Bundle(
  open val title: String,
  val appsList: List<App>,
  val type: Type,
  val bundleIcon: String? = null,
  val graphic: String? = null,
  val bundleAction: BundleAction? = null,
  val view: String? = null
)

data class MyAppsBundle(
  val installedApps: List<MyAppsApp>,
  override val title: String
) :
  Bundle(title, emptyList(), Type.MY_APPS)

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