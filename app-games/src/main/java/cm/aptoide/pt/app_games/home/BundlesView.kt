package cm.aptoide.pt.app_games.home

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavGraphBuilder
import cm.aptoide.pt.app_games.AptoideAsyncImage
import cm.aptoide.pt.app_games.R
import cm.aptoide.pt.app_games.categories.presentation.CategoriesBundle
import cm.aptoide.pt.app_games.editorial.EditorialBundle
import cm.aptoide.pt.app_games.feature_apps.presentation.AppsGridBundle
import cm.aptoide.pt.app_games.feature_apps.presentation.CarouselBundle
import cm.aptoide.pt.app_games.feature_apps.presentation.CarouselLargeBundle
import cm.aptoide.pt.app_games.feature_apps.presentation.MyGamesBundleView
import cm.aptoide.pt.app_games.feature_apps.presentation.PublisherTakeover
import cm.aptoide.pt.app_games.feature_apps.presentation.perCarouselViewModel
import cm.aptoide.pt.app_games.theme.AppTheme
import cm.aptoide.pt.app_games.theme.gray5
import cm.aptoide.pt.aptoide_ui.animations.staticComposable
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.Type
import cm.aptoide.pt.feature_home.presentation.BundlesViewUiState
import cm.aptoide.pt.feature_home.presentation.BundlesViewUiStateType
import cm.aptoide.pt.feature_home.presentation.bundlesList
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

val translatedTitles = mapOf(
  "Just Arrived" to R.string.fixed_bundle_just_arrived_title,
  "My Games" to R.string.fixed_bundle_my_games_title,
  "Editors' Choice" to R.string.fixed_bundle_editors_choice_title,
  "Editorial" to R.string.fixed_bundle_editorial_title,
  "Trending" to R.string.fixed_bundle_trending_title
)

const val gamesRoute = "gamesView"

fun NavGraphBuilder.gamesScreen(
  navigate: (String) -> Unit,
) = staticComposable(
  gamesRoute
) {
  BundlesScreen(
    navigate = navigate,
  )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BundlesScreen(
  navigate: (String) -> Unit,
) {
  val (viewState, loadFreshHomeBundles) = bundlesList(context = "home_games")

  val isRefreshing = (viewState.type == BundlesViewUiStateType.RELOADING)

  val pullRefreshState = rememberPullRefreshState(
    refreshing = isRefreshing,
    onRefresh = loadFreshHomeBundles
  )

  Box(
    modifier = Modifier
      .pullRefresh(pullRefreshState)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.TopCenter)
    ) {
      when (viewState.type) {
        BundlesViewUiStateType.LOADING,
        BundlesViewUiStateType.RELOADING,
        -> LoadingView()

        BundlesViewUiStateType.NO_CONNECTION -> NoConnectionView(
          onRetryClick = {
            loadFreshHomeBundles()
          }
        )

        BundlesViewUiStateType.ERROR -> GenericErrorView(
          onRetryClick = loadFreshHomeBundles
        )

        BundlesViewUiStateType.IDLE -> BundlesView(
          viewState,
          navigate
        )
      }
    }

    when (viewState.type) {
      BundlesViewUiStateType.IDLE,
      BundlesViewUiStateType.LOADING,
      BundlesViewUiStateType.RELOADING,
      -> PullRefreshIndicator(
        refreshing = isRefreshing,
        state = pullRefreshState,
        modifier = Modifier.align(Alignment.TopCenter)
      )

      else -> {}
    }
  }
}

@Composable
fun BundlesView(
  viewState: BundlesViewUiState,
  navigate: (String) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.TopCenter)
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.TopCenter)
    ) {
      items(viewState.bundles) {
        when (it.type) {
          Type.APP_GRID -> AppsGridBundle(
            bundle = it,
            navigate = navigate,
          )

          Type.EDITORIAL -> EditorialBundle(
            bundle = it,
          )

          Type.CAROUSEL -> CarouselBundle(
            bundle = it,
            navigate = navigate,
          )

          Type.CAROUSEL_LARGE -> CarouselLargeBundle(
            bundle = it,
            navigate = navigate,
          )

          Type.CATEGORIES -> CategoriesBundle(
            bundle = it,
            navigate = navigate
          )

          Type.MY_GAMES -> MyGamesBundleView(
            title = it.title.translateOrKeep(LocalContext.current),
            icon = it.bundleIcon,
          )

          Type.PUBLISHER_TAKEOVER -> PublisherTakeover(
            bundle = it,
            navigate = navigate,
          )

          else -> Unit
        }
      }
    }
  }
}

fun String.translateOrKeep(localContext: Context): String {
  return translatedTitles[this]
    ?.let { localContext.getString(it) }
    ?: this
}

@Composable
fun LoadingBundleView(height: Dp) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(height),
    contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator()
  }
}

@Composable
fun EmptyBundleView(height: Dp) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(height),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Image(
      imageVector = AppTheme.icons.PlanetSearch,
      contentDescription = null
    )
    Text(
      modifier = Modifier.padding(all = 24.dp),
      text = stringResource(R.string.editorials_view_no_content_message),
      style = AppTheme.typography.headlineTitleText,
      textAlign = TextAlign.Center,
      color = gray5,
    )
  }
}

@Composable
fun BundleHeader(
  bundle: Bundle,
  titleColor: Color = Color.Unspecified,
  actionColor: Color = AppTheme.colors.moreAppsViewBackColor,
) {
  val title = bundle.title
  Row(
    modifier = Modifier
      .clearAndSetSemantics {
        heading()
        contentDescription = "$title bundle"
      }
      .fillMaxWidth()
      .padding(all = 16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f, fill = false)
    ) {
      bundle.bundleIcon?.let {
        AptoideAsyncImage(
          modifier = Modifier
            .padding(end = 8.dp)
            .size(24.dp),
          data = bundle.bundleIcon,
          contentDescription = null,
        )
      }
      Text(
        text = title,
        maxLines = 2,
        modifier = Modifier
          .clearAndSetSemantics { },
        style = AppTheme.typography.headlineTitleText,
        color = titleColor,
        overflow = TextOverflow.Ellipsis
      )
    }
    if (bundle.hasMoreAction) {
      SeeMoreView(
        actionColor = actionColor
      )
    }
  }
}

@Composable
fun SeeMoreView(
  modifier: Modifier = Modifier,
  actionColor: Color = AppTheme.colors.moreAppsViewBackColor,
) {
  Row(
    modifier = modifier
      .padding(start = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = stringResource(R.string.button_see_all_title),
      color = actionColor,
      modifier = Modifier.padding(end = 4.dp),
      style = AppTheme.typography.headlineTitleText,
      overflow = TextOverflow.Ellipsis,
      maxLines = 2,
    )
    Icon(
      modifier = Modifier
        .size(18.dp)
        .requiredSize(18.dp),
      imageVector = Icons.Default.ArrowForward,
      contentDescription = null,
      tint = actionColor
    )
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPagerView(
  appsList: List<App>,
  modifier: Modifier = Modifier,
  content: @Composable (modifier: Modifier, page: Int, app: App) -> Unit,
) {

  val screenWidth = LocalConfiguration.current.screenWidthDp.dp
  val padding = (screenWidth - 280.dp - 16.dp) / 2 + 8.dp
  val contentPadding = PaddingValues(
    start = padding,
    end = padding
  )

  val initialItem = calculateLoopedBundleInitialItem(appsList.size)

  val pagerState = rememberPagerState(
    initialPage = initialItem,
    pageCount = { Int.MAX_VALUE }
  )
  val scope = rememberCoroutineScope()

  val autoScrollViewModel = perCarouselViewModel(carouselTag = appsList.hashCode().toString())
  val currentPage by autoScrollViewModel.uiState.collectAsState()

  /*Used when the current page state is changed in the AutoScrollViewModel*/
  LaunchedEffect(currentPage) {
    currentPage?.let {
      scope.launch {
        pagerState.animateScrollToPage(it)
      }
    }
  }

  /* Used to update the current page in the autoscroll ViewModel */
  LaunchedEffect(pagerState.isScrollInProgress) {
    if (pagerState.isScrollInProgress)
      autoScrollViewModel.cancel()
    else
      autoScrollViewModel.start()
  }

  LaunchedEffect(pagerState.currentPage) {
    autoScrollViewModel.updateCurrentItem(pagerState.currentPage)
  }

  HorizontalPager(
    pageSpacing = 16.dp,
    contentPadding = contentPadding,
    state = pagerState,
    modifier = modifier.semantics {
      collectionInfo = CollectionInfo(1, appsList.size)
    }
  ) { index ->
    val page = (index - 1).floorMod(appsList.size)
    val app = appsList[page]
    val pageModifier = Modifier
      .graphicsLayer {
        val pageOffset = (
          (pagerState.currentPage - index) + pagerState
            .currentPageOffsetFraction
          ).absoluteValue

        lerp(
          start = 0.87f,
          stop = 1f,
          fraction = 1f - pageOffset.coerceIn(0f, 1f)
        ).also { scale ->
          scaleY = scale
        }
      }
    content(pageModifier, page, app)
  }
}

@Composable
fun LoadingView() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    CircularProgressIndicator()
  }
}

private fun calculateLoopedBundleInitialItem(appsListSize: Int): Int {
  val numberOfLists: Int = Int.MAX_VALUE / appsListSize
  val middleList: Int = numberOfLists / 2
  val middleListLastIndex: Int = middleList * appsListSize
  return (middleListLastIndex - appsListSize) + 2
}

private fun Int.floorMod(other: Int): Int = when (other) {
  0 -> this
  else -> this - floorDiv(other) * other
}
