package cm.aptoide.pt.feature_apps.domain

import cm.aptoide.pt.feature_apps.data.App

data class Bundle(val title: String, val appsList: List<App>, val type: Type)

enum class Type {
  FEATURE_GRAPHIC, APP_GRID, ESKILLS, UNKNOWN_BUNDLE
}