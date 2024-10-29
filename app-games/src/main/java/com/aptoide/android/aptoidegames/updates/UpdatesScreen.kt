package com.aptoide.android.aptoidegames.updates

import androidx.compose.runtime.Composable
import androidx.navigation.navDeepLink
import cm.aptoide.pt.extensions.ScreenData
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics

const val updatesRoute = "updates"

fun updatesScreen() = ScreenData.withAnalytics(
  route = updatesRoute,
  screenAnalyticsName = "Updates",
  deepLinks = listOf(navDeepLink { uriPattern = BuildConfig.DEEP_LINK_SCHEMA + updatesRoute })
) { _, navigate, _ ->
  UpdatesScreen(navigate = navigate)
}

@Composable
fun UpdatesScreen(
  navigate: (String) -> Unit,
) {
}
