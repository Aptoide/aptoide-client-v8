package com.aptoide.android.aptoidegames.analytics.presentation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cm.aptoide.pt.extensions.ScreenData
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.dto.BundleMeta

object AnalyticsContext {
  val current: AnalyticsUIContext
    @Composable
    @ReadOnlyComposable
    get() = LocalAnalyticsContext.current
}

private val LocalAnalyticsContext = staticCompositionLocalOf { AnalyticsUIContext.Empty }

private const val PREV_SCREEN_PARAM = "previousScreen"
private const val BUNDLE_META_PARAM = "bundleMeta"

fun ScreenData.Companion.withAnalytics(
  route: String,
  screenAnalyticsName: String,
  arguments: List<NamedNavArgument> = emptyList(),
  deepLinks: List<NavDeepLink> = emptyList(),
  content: @Composable (Bundle?, (String) -> Unit, () -> Unit) -> Unit,
): ScreenData {
  return ScreenData(
    route = route
      .withPrevScreen("{$PREV_SCREEN_PARAM}")
      .withBundleMeta("{$BUNDLE_META_PARAM}"),
    arguments = arguments + listOf(
      navArgument(PREV_SCREEN_PARAM) {
        type = NavType.StringType
        nullable = true
      },
      navArgument(BUNDLE_META_PARAM) {
        type = NavType.StringType
        nullable = true
      }
    ),
    deepLinks = deepLinks,
    content = { args, navigate, goBack ->
      val previousScreen = args?.getString(PREV_SCREEN_PARAM)
      val bundleMeta = args?.getString(BUNDLE_META_PARAM)?.let(BundleMeta::fromString)
      CompositionLocalProvider(
        LocalAnalyticsContext provides AnalyticsUIContext(
          currentScreen = screenAnalyticsName,
          previousScreen = previousScreen,
          bundleMeta = bundleMeta
        )
      ) {
        content(
          args,
          { navigate(it.withPrevScreen(screenAnalyticsName).withBundleMeta(bundleMeta)) },
          goBack
        )
      }
    }
  )
}

@Composable
fun OverrideAnalyticsBundleMeta(
  bundleMeta: BundleMeta,
  navigate: (String) -> Unit,
  content: @Composable ((String) -> Unit) -> Unit,
) {
  val current = LocalAnalyticsContext.current
  CompositionLocalProvider(
    LocalAnalyticsContext provides AnalyticsUIContext(
      currentScreen = current.currentScreen,
      previousScreen = current.previousScreen,
      bundleMeta = bundleMeta
    )
  ) {
    content {
      navigate(it.withBundleMeta(bundleMeta))
    }
  }
}

fun String.withBundleMeta(bundleMeta: BundleMeta?) = withBundleMeta(bundleMeta?.toString())

private fun String.withPrevScreen(previousScreen: String) =
  withParameter(PREV_SCREEN_PARAM, previousScreen)

private fun String.withBundleMeta(bundleMeta: String?) =
  withParameter(BUNDLE_META_PARAM, bundleMeta)

private fun String.withParameter(name: String, value: String?) = when {
  value.isNullOrBlank() -> this
  contains(name) -> this
  contains("?") -> "$this&$name=$value"
  else -> "$this?$name=$value"
}
