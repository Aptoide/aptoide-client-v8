package com.aptoide.android.aptoidegames.analytics.presentation

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_campaigns.UTMInfo
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.dto.BundleMeta
import com.aptoide.android.aptoidegames.analytics.dto.SearchMeta
import com.aptoide.android.aptoidegames.mmp.LocalUTMInfo
import com.aptoide.android.aptoidegames.mmp.UTMContext

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
private const val IS_APKFY_PARAM = "isApkfy"
private const val IS_PLAY_AND_EARN_PARAM = "isPlayAndEarn"
private const val HOME_TAB_PARAM = "HomeTab"
private const val UTM_INFO_PARAM = "utmInfo"

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
      .withItemPosition("{$ITEM_POSITION_PARAM}")
      .withApkfy("{$IS_APKFY_PARAM}")
      .withHomeTab("{$HOME_TAB_PARAM}")
      .withUtmInfo("{$UTM_INFO_PARAM}"),
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
        type = NavType.StringType
        nullable = true
      },
      navArgument(IS_APKFY_PARAM) {
        type = NavType.BoolType
        defaultValue = false
      },
      navArgument(UTM_INFO_PARAM) {
        type = NavType.StringType
        nullable = true
      }
    ),
    deepLinks = deepLinks.map {
      navDeepLink { uriPattern = it.uriPattern?.withPrevScreen("{$PREV_SCREEN_PARAM}") }
    },
    content = { args, navigate, goBack ->
      val previousScreen = args?.getString(PREV_SCREEN_PARAM)
      val bundleMeta = args?.getString(BUNDLE_META_PARAM)?.let(BundleMeta::fromString)
      val searchMeta = args?.getString(SEARCH_META_PARAM)?.let(SearchMeta::fromString)
      val itemPosition = args?.getString(ITEM_POSITION_PARAM)?.toIntOrNull()
      val isApkfy = args?.getBoolean(IS_APKFY_PARAM, false) ?: false
      val homeTab = args?.getString(HOME_TAB_PARAM, null)
      val utmInfo = args?.getString(UTM_INFO_PARAM)?.let(UTMInfo::fromString)

      val utmContext = UTMContext.current

      //So that UI drawn outside of this screen is aware of the current screen
      AnalyticsContext.current.currentScreen = screenAnalyticsName

      CompositionLocalProvider(
        LocalAnalyticsContext provides AnalyticsUIContext(
          currentScreen = screenAnalyticsName,
          previousScreen = previousScreen,
          bundleMeta = bundleMeta,
          searchMeta = searchMeta,
          itemPosition = itemPosition,
          isApkfy = isApkfy,
          homeTab = homeTab,
        ),
        LocalUTMInfo provides (utmInfo ?: utmContext)
      ) {
        content(
          args,
          {
            it.withPrevScreen(screenAnalyticsName)
              .withBundleMeta(bundleMeta)
              .withSearchMeta(searchMeta)
              .withItemPosition(itemPosition)
              .withApkfy(isApkfy)
              .withHomeTab(homeTab)
              .withUtmInfo(utmInfo)
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
      itemPosition = current.itemPosition,
      isApkfy = false,
      homeTab = current.homeTab
    )
  ) {
    content {
      navigate(it.withBundleMeta(bundleMeta))
    }
  }
}

@Composable
fun OverrideAnalyticsScreen(
  currentScreen: String,
  navigate: (String) -> Unit,
  content: @Composable ((String) -> Unit) -> Unit,
) {
  val current = LocalAnalyticsContext.current
  CompositionLocalProvider(
    LocalAnalyticsContext provides AnalyticsUIContext(
      currentScreen = currentScreen,
      previousScreen = current.previousScreen,
      bundleMeta = current.bundleMeta,
      searchMeta = current.searchMeta,
      itemPosition = current.itemPosition,
      isApkfy = false,
      homeTab = null
    )
  ) {
    content {
      navigate(it.withPrevScreen(currentScreen))
    }
  }
}

@Composable
fun OverrideAnalyticsHomeTab(
  homeTab: String,
  navigate: (String) -> Unit,
  content: @Composable ((String) -> Unit) -> Unit,
) {
  val current = LocalAnalyticsContext.current
  CompositionLocalProvider(
    LocalAnalyticsContext provides AnalyticsUIContext(
      currentScreen = current.currentScreen,
      previousScreen = current.previousScreen,
      bundleMeta = current.bundleMeta,
      searchMeta = current.searchMeta,
      itemPosition = current.itemPosition,
      isApkfy = false,
      homeTab = homeTab
    )
  ) {
    content {
      navigate(it.withHomeTab(homeTab))
    }
  }
}

@Composable
fun OverrideAnalyticsSearchMeta(
  searchMeta: SearchMeta?,
  navigate: (String) -> Unit,
  content: @Composable ((String) -> Unit) -> Unit,
) {
  val current = LocalAnalyticsContext.current
  CompositionLocalProvider(
    LocalAnalyticsContext provides AnalyticsUIContext(
      currentScreen = current.currentScreen,
      previousScreen = current.previousScreen,
      bundleMeta = current.bundleMeta,
      searchMeta = searchMeta,
      itemPosition = current.itemPosition,
      isApkfy = false,
      homeTab = null
    )
  ) {
    content {
      navigate(it.withSearchMeta(searchMeta))
    }
  }
}

@Composable
fun InitialAnalyticsMeta(
  screenAnalyticsName: String,
  navigate: (String) -> Unit,
  content: @Composable ((String) -> Unit) -> Unit,
) {
  //So that UI drawn outside of this screen is aware of the current screen
  AnalyticsContext.current.currentScreen = screenAnalyticsName

  CompositionLocalProvider(
    LocalAnalyticsContext provides AnalyticsUIContext(
      currentScreen = screenAnalyticsName,
      previousScreen = null,
      bundleMeta = null,
      searchMeta = null,
      itemPosition = null,
      isApkfy = false,
      homeTab = null
    )
  ) {
    content {
      navigate(it.withPrevScreen(screenAnalyticsName))
    }
  }
}

@Composable
fun OverrideAnalyticsAPKFY(
  navigate: (String) -> Unit,
  content: @Composable ((String) -> Unit) -> Unit,
) {
  val current = LocalAnalyticsContext.current
  CompositionLocalProvider(
    LocalAnalyticsContext provides AnalyticsUIContext(
      currentScreen = current.currentScreen,
      previousScreen = current.previousScreen,
      bundleMeta = null,
      searchMeta = null,
      itemPosition = null,
      isApkfy = true,
      homeTab = null
    )
  ) {
    content {
      navigate(it.withApkfy(true).withPrevScreen(current.currentScreen))
    }
  }
}

@Composable
fun OverrideAnalyticsPlayAndEarn(
  navigate: (String) -> Unit,
  content: @Composable ((String) -> Unit) -> Unit,
) {
  val current = LocalAnalyticsContext.current
  CompositionLocalProvider(
    LocalAnalyticsContext provides AnalyticsUIContext(
      currentScreen = current.currentScreen,
      previousScreen = current.previousScreen,
      bundleMeta = null,
      searchMeta = null,
      itemPosition = null,
      isApkfy = false,
      homeTab = null,
      isPlayAndEarn = true
    )
  ) {
    content {
      navigate(it.withPlayAndEarn(true).withPrevScreen(current.currentScreen))
    }
  }
}

fun String.withBundleMeta(bundleMeta: BundleMeta?) = withBundleMeta(bundleMeta?.toString())

fun String.withSearchMeta(searchMeta: SearchMeta?) = withSearchMeta(searchMeta?.toString())

fun String.withItemPosition(itemPosition: Int?) = withItemPosition(itemPosition?.toString())

fun String.withApkfy(isApkfy: Boolean?) = withApkfy(isApkfy?.toString())

fun String.withPlayAndEarn(isPlayAndEarn: Boolean?) = withPlayAndEarn(isPlayAndEarn?.toString())

fun String.withPrevScreen(previousScreen: String) =
  withParameter(PREV_SCREEN_PARAM, previousScreen)

private fun String.withBundleMeta(bundleMeta: String?) =
  withParameter(BUNDLE_META_PARAM, bundleMeta)

private fun String.withSearchMeta(searchMeta: String?) =
  withParameter(SEARCH_META_PARAM, searchMeta)

fun String.withItemPosition(itemPosition: String?) =
  withParameter(ITEM_POSITION_PARAM, itemPosition)

fun String.withApkfy(isApkfy: String?) =
  withParameter(IS_APKFY_PARAM, isApkfy)

fun String.withPlayAndEarn(isPlayAndEarn: String?) =
  withParameter(IS_PLAY_AND_EARN_PARAM, isPlayAndEarn)

fun String.withHomeTab(homeTab: String?) =
  withParameter(HOME_TAB_PARAM, homeTab)

fun String.withUtmInfo(utmInfo: UTMInfo?) = withUtmInfo(utmInfo?.toString())

private fun String.withUtmInfo(utmInfo: String?) =
  withParameter(UTM_INFO_PARAM, utmInfo)

fun String.withParameter(
  name: String,
  value: String?,
) = when {
  value.isNullOrBlank() -> this
  contains(name) -> this
  contains("?") -> "$this&$name=$value"
  else -> "$this?$name=$value"
}

fun Uri.withPrevScreen(previousScreen: String) =
  this.takeIf { getQueryParameter(PREV_SCREEN_PARAM) == null }
    ?.buildUpon()
    ?.appendQueryParameter(PREV_SCREEN_PARAM, previousScreen)
    ?.build() ?: this
