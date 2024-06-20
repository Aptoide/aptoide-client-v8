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
import com.aptoide.android.aptoidegames.analytics.dto.SearchMeta

object AnalyticsContext {
  val current: AnalyticsUIContext
    @Composable
    @ReadOnlyComposable
    get() = LocalAnalyticsContext.current
}

private val LocalAnalyticsContext = staticCompositionLocalOf { AnalyticsUIContext.Empty }

private const val PREV_SCREEN_PARAM = "previousScreen"
private const val BUNDLE_META_PARAM = "bundleMeta"
private const val SEARCH_META_PARAM = "searchMeta"
private const val ITEM_POSITION_PARAM = "itemPosition"

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
      .withBundleMeta("{$BUNDLE_META_PARAM}")
      .withSearchMeta("{$SEARCH_META_PARAM}")
      .withItemPosition("{$ITEM_POSITION_PARAM}"),
    arguments = arguments + listOf(
      navArgument(PREV_SCREEN_PARAM) {
        type = NavType.StringType
        nullable = true
      },
      navArgument(BUNDLE_META_PARAM) {
        type = NavType.StringType
        nullable = true
      },
      navArgument(SEARCH_META_PARAM) {
        type = NavType.StringType
        nullable = true
      },
      navArgument(ITEM_POSITION_PARAM) {
        type = NavType.IntType
        nullable = true
      }
    ),
    deepLinks = deepLinks,
    content = { args, navigate, goBack ->
      val previousScreen = args?.getString(PREV_SCREEN_PARAM)
      val bundleMeta = args?.getString(BUNDLE_META_PARAM)?.let(BundleMeta::fromString)
      val searchMeta = args?.getString(SEARCH_META_PARAM)?.let(SearchMeta::fromString)
      val itemPosition = args?.getString(ITEM_POSITION_PARAM)?.toIntOrNull()
      CompositionLocalProvider(
        LocalAnalyticsContext provides AnalyticsUIContext(
          currentScreen = screenAnalyticsName,
          previousScreen = previousScreen,
          bundleMeta = bundleMeta,
          searchMeta = searchMeta,
          itemPosition = itemPosition
        )
      ) {
        content(
          args,
          {
            it.withPrevScreen(screenAnalyticsName)
              .withBundleMeta(bundleMeta)
              .withSearchMeta(searchMeta)
              .withItemPosition(itemPosition)
              .also(navigate)
          },
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
      bundleMeta = bundleMeta,
      searchMeta = current.searchMeta,
      itemPosition = current.itemPosition
    )
  ) {
    content {
      navigate(it.withBundleMeta(bundleMeta))
    }
  }
}

fun String.withBundleMeta(bundleMeta: BundleMeta?) = withBundleMeta(bundleMeta?.toString())

fun String.withSearchMeta(searchMeta: SearchMeta?) = withSearchMeta(searchMeta?.toString())

fun String.withItemPosition(itemPosition: Int?) = withItemPosition(itemPosition?.toString())

private fun String.withPrevScreen(previousScreen: String) =
  withParameter(PREV_SCREEN_PARAM, previousScreen)

private fun String.withBundleMeta(bundleMeta: String?) =
  withParameter(BUNDLE_META_PARAM, bundleMeta)

private fun String.withSearchMeta(searchMeta: String?) =
  withParameter(SEARCH_META_PARAM, searchMeta)

fun String.withItemPosition(itemPosition: String?) =
  withParameter(ITEM_POSITION_PARAM, itemPosition)

private fun String.withParameter(name: String, value: String?) = when {
  value.isNullOrBlank() -> this
  contains(name) -> this
  contains("?") -> "$this&$name=$value"
  else -> "$this?$name=$value"
}
