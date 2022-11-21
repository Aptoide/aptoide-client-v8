package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.MyAppsApp
import cm.aptoide.pt.feature_editorial.data.ArticleType

open class Bundle(
  val title: String,
  val appsList: List<App>,
  val type: Type,
  val bundleIcon: String? = null,
  val graphic: String? = null,
  val bundleAction: BundleAction? = null
)

data class EditorialBundle(
  val articleId: String,
  val editorialTitle: String,
  val summary: String,
  val image: String,
  val subtype: ArticleType,
  val date: String,
  val views: Long,
  val reactionsNumber: Int,
) :
  Bundle(editorialTitle, emptyList(), Type.EDITORIAL)

data class MyAppsBundle(
  val installedApps: List<MyAppsApp>
) :
  Bundle("", emptyList(), Type.MY_APPS)

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