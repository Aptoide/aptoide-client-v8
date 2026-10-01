package com.aptoide.android.aptoidegames.appview

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.domain.AppOrigin

// The Play catalog describes an app without rating, downloads, version, size or dates. Such
// gaps are hidden rather than shown as dashes and zeros. Only apps read from the new services
// are ever missing them, so a v7 app shows everything, as it always did.

val App.showsRating: Boolean
  get() = origin == AppOrigin.V7 || pRating.totalVotes > 0 || pRating.avgRating > 0.0

val App.showsDownloads: Boolean
  get() = origin == AppOrigin.V7 || pDownloads > 0

val App.showsVersion: Boolean
  get() = origin == AppOrigin.V7 || versionName.isNotBlank()

val App.showsSize: Boolean
  get() = origin == AppOrigin.V7 || appSize > 0

val App.showsReleaseDate: Boolean
  get() = origin == AppOrigin.V7 || releaseDate != null

val App.showsUpdateDate: Boolean
  get() = origin == AppOrigin.V7 || updateDate != null
