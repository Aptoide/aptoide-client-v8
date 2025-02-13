package com.aptoide.android.aptoidegames.home

import cm.aptoide.pt.extensions.ScreenData
import com.aptoide.android.aptoidegames.analytics.presentation.InitialAnalyticsMeta

const val gamesRoute = "games"

fun gamesScreen() = ScreenData(
  route = gamesRoute,
) { _, navigate, _ ->
  InitialAnalyticsMeta(
    screenAnalyticsName = "Home",
    navigate = navigate
  ) {
    BundlesScreen(
      navigate = it,
    )
  }
}
