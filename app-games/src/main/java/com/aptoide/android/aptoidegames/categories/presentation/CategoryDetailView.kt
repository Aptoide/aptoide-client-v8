package com.aptoide.android.aptoidegames.categories.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppItem
import com.aptoide.android.aptoidegames.feature_apps.presentation.LargeAppItem
import com.aptoide.android.aptoidegames.home.EmptyView
import com.aptoide.android.aptoidegames.home.GenericErrorView
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.home.NoConnectionView
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.animatedComposable
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.AppsListUiStateProvider
import cm.aptoide.pt.feature_apps.presentation.categoryApps

const val categoryDetailRoute = "category/{title}/{name}"

fun NavGraphBuilder.categoryDetailScreen(
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) = animatedComposable(categoryDetailRoute) {
  val categoryTitle = it.arguments?.getString("title")!!
  val categoryName = it.arguments?.getString("name")!!
  val (uiState, reload) = categoryApps(categoryName)

  CategoryDetailView(
    title = categoryTitle,
    categoryName = categoryName,
    uiState = uiState,
    onError = reload,
    navigateBack = navigateBack,
    navigate = navigate,
  )
}

fun buildCategoryDetailRoute(
  title: String,
  name: String,
) = "category/$title/$name"

@Composable
fun CategoryDetailView(
  title: String,
  categoryName: String,
  uiState: AppsListUiState,
  onError: () -> Unit,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) {

  val navigateToApp =
    { app: App -> navigate(buildAppViewRoute(app.packageName)) }

  CategoryDetailViewContent(categoryName = categoryName) {
    AppGamesTopBar(
      navigateBack = {
        navigateBack()
      },
      title = title
    )
    when (uiState) {
      is AppsListUiState.Loading -> LoadingView()
      is AppsListUiState.Empty -> EmptyView(text = stringResource(R.string.empty_category_body))
      is AppsListUiState.NoConnection -> NoConnectionView(onRetryClick = onError)
      is AppsListUiState.Error -> GenericErrorView(onError)
      is AppsListUiState.Idle -> CategoryAppsList(
        size = uiState.apps.size,
      ) {
        itemsIndexed(
          uiState.apps,
          key = { _, app -> app.packageName }
        )
        { index, app ->

          val installViewShort: @Composable () -> Unit = {
            InstallViewShort(
              app = app,
              onInstallStarted = {}
            )
          }
          if (index == 0) {
            LargeAppItem(
              app = app,
              onClick = { navigateToApp(app) }
            ) {
              installViewShort()
            }
          } else {
            AppItem(
              app = app,
              onClick = { navigateToApp(app) },
            ) {
              installViewShort()
            }
          }
        }
      }
    }
  }
}

@Composable
fun CategoryDetailViewContent(
  categoryName: String,
  content: @Composable ColumnScope.() -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    content()
  }
}

@Composable
fun CategoryAppsList(
  size: Int,
  content: LazyListScope.() -> Unit,
) {
  LazyColumn(
    modifier = Modifier
      .semantics { collectionInfo = CollectionInfo(size, 1) }
      .padding(start = 16.dp, end = 16.dp)
      .wrapContentSize(Alignment.TopCenter)
  ) {
    content()
  }
}

@PreviewDark
@Composable
fun CategoryDetailViewPreview(
  @PreviewParameter(AppsListUiStateProvider::class) uiState: AppsListUiState,
) {
  CategoryDetailView(
    title = "Action",
    categoryName = "Action",
    uiState = uiState,
    onError = {},
    navigateBack = {},
    navigate = {},
  )
}
