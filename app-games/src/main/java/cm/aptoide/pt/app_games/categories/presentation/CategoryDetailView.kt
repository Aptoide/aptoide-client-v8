package cm.aptoide.pt.app_games.categories.presentation

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import cm.aptoide.pt.app_games.R
import cm.aptoide.pt.app_games.appview.buildAppViewRoute
import cm.aptoide.pt.app_games.feature_apps.presentation.AppItem
import cm.aptoide.pt.app_games.feature_apps.presentation.LargeAppItem
import cm.aptoide.pt.app_games.home.EmptyView
import cm.aptoide.pt.app_games.home.GenericErrorView
import cm.aptoide.pt.app_games.home.LoadingView
import cm.aptoide.pt.app_games.home.NoConnectionView
import cm.aptoide.pt.app_games.installer.presentation.InstallViewShort
import cm.aptoide.pt.app_games.toolbar.AppGamesTopBar
import cm.aptoide.pt.aptoide_ui.animations.animatedComposable
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.categoryApps
import kotlin.random.Random

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

@PreviewAll
@Composable
fun CategoryDetailViewPreview() {
  val uiStateFake = AppsListUiState.Idle(List(Random.nextInt(until = 20)) {
    randomApp
  })
  CategoryDetailView(
    title = "Action",
    categoryName = "Action",
    uiState = uiStateFake,
    onError = {},
    navigateBack = {},
    navigate = {},
  )
}
