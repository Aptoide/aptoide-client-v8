package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import cm.aptoide.pt.aptoide_ui.animations.animatedComposable
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.getRandomString
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.rememberAppsByTag
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.home.GenericErrorView
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.home.NoConnectionView
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar
import kotlin.random.Random

const val seeMoreRoute =
  "seeMore/{title}/{tag}?originSection={originSection}&bundleSource={bundleSource}"

fun NavGraphBuilder.seeMoreScreen(
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) = animatedComposable(
  seeMoreRoute,
  arguments = listOf(
    navArgument("originSection") { nullable = true },
    navArgument("bundleSource") { nullable = true },
  ),
  deepLinks = listOf(navDeepLink { uriPattern = BuildConfig.DEEP_LINK_SCHEMA + seeMoreRoute })
) {
  val bundleTitle = it.arguments?.getString("title")!!
  val bundleTag = it.arguments?.getString("tag")!!

  MoreBundleView(
    title = bundleTitle,
    bundleTag = bundleTag,
    navigateBack = navigateBack,
    navigate = navigate,
  )
}

fun buildSeeMoreRoute(
  title: String,
  bundleTag: String,
  originSection: String? = null,
  bundleSource: String? = null,
) = "seeMore/$title/$bundleTag?originSection=$originSection&bundleSource=$bundleSource"

@Composable
fun MoreBundleView(
  title: String,
  bundleTag: String,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) {
  val (uiState, reload) = rememberAppsByTag(bundleTag)

  MoreBundleViewContent(
    uiState = uiState,
    title = title,
    reload = reload,
    noNetworkReload = {
      reload()
    },
    navigateBack = {
      navigateBack()
    },
    navigate = navigate,
  )
}

@Composable
private fun MoreBundleViewContent(
  uiState: AppsListUiState,
  title: String,
  reload: () -> Unit,
  noNetworkReload: () -> Unit,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    AppGamesTopBar(navigateBack = navigateBack, title = title)
    when (uiState) {
      AppsListUiState.Loading -> LoadingView()
      AppsListUiState.NoConnection -> NoConnectionView(onRetryClick = noNetworkReload)
      AppsListUiState.Error -> GenericErrorView(reload)
      AppsListUiState.Empty -> AppsList(
        appList = emptyList(),
        navigate = navigate,
      )

      is AppsListUiState.Idle -> AppsList(
        appList = uiState.apps,
        navigate = navigate,
      )
    }
  }
}

@Composable
private fun AppsList(
  appList: List<App>,
  navigate: (String) -> Unit,
) {
  Spacer(modifier = Modifier.fillMaxWidth())
  LazyColumn(
    modifier = Modifier
      .semantics { collectionInfo = CollectionInfo(appList.size, 1) }
      .padding(start = 16.dp, end = 16.dp)
      .wrapContentSize(Alignment.TopCenter)
  ) {
    items(appList) { app ->
      AppItem(
        app = app,
        onClick = { navigate(buildAppViewRoute(app.packageName)) },
      ) {
        InstallViewShort(app)
      }
    }
  }
}

@PreviewDark
@Composable
private fun MoreBundleViewIdlePreview() {
  AptoideTheme {
    MoreBundleViewContent(
      uiState = AppsListUiState.Idle(List(size = Random.nextInt(15)) { randomApp }),
      title = getRandomString(range = 1..5, capitalize = true),
      reload = {},
      noNetworkReload = {},
      navigateBack = {},
      navigate = {},
    )
  }
}

@PreviewDark
@Composable
private fun MoreBundleViewPreview(
  @PreviewParameter(AppsListUiStateProvider::class) uiState: AppsListUiState,
) {
  AptoideTheme {
    MoreBundleViewContent(
      uiState = uiState,
      title = getRandomString(range = 1..5, capitalize = true),
      reload = {},
      noNetworkReload = {},
      navigateBack = {},
      navigate = {},
    )
  }
}

class AppsListUiStateProvider : PreviewParameterProvider<AppsListUiState> {
  override val values: Sequence<AppsListUiState> = sequenceOf(
    AppsListUiState.Idle(List(size = Random.nextInt(15)) { randomApp }),
    AppsListUiState.Loading,
    AppsListUiState.Empty,
    AppsListUiState.NoConnection,
    AppsListUiState.Error
  )
}
