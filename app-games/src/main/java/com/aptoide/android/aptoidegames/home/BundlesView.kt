package com.aptoide.android.aptoidegames.home

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
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
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.Type
import cm.aptoide.pt.feature_home.presentation.BundlesViewUiState
import cm.aptoide.pt.feature_home.presentation.BundlesViewUiStateType
import cm.aptoide.pt.feature_home.presentation.bundlesList
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsBundleMeta
import com.aptoide.android.aptoidegames.analytics.presentation.SwipeListener
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withBundleMeta
import com.aptoide.android.aptoidegames.categories.presentation.CategoriesBundle
import com.aptoide.android.aptoidegames.drawables.icons.getForward
import com.aptoide.android.aptoidegames.editorial.EditorialBundle
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppsGridBundle
import com.aptoide.android.aptoidegames.feature_apps.presentation.BonusSectionView
import com.aptoide.android.aptoidegames.feature_apps.presentation.CarouselBundle
import com.aptoide.android.aptoidegames.feature_apps.presentation.CarouselLargeBundle
import com.aptoide.android.aptoidegames.feature_apps.presentation.MyGamesBundleView
import com.aptoide.android.aptoidegames.feature_apps.presentation.PublisherTakeOverBundle
import com.aptoide.android.aptoidegames.feature_apps.presentation.buildSeeMoreRoute
import com.aptoide.android.aptoidegames.feature_apps.presentation.perCarouselViewModel
import com.aptoide.android.aptoidegames.home.analytics.meta
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
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

fun gamesScreen() = ScreenData.withAnalytics(
  route = gamesRoute,
  screenAnalyticsName = "Home"
) { _, navigate, _ ->
  BundlesScreen(
    navigate = navigate,
  )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BundlesScreen(
  navigate: (String) -> Unit,
) {
  val (viewState, loadFreshHomeBundles) = bundlesList()

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
      items(viewState.bundles.size + 1) { index ->
        when (index) {
          1 -> BonusSectionView()
          else -> {
            val dataIndex = if (index > 1) index - 1 else index
            val item = viewState.bundles.getOrNull(dataIndex)
            if (item != null) {
              OverrideAnalyticsBundleMeta(item.meta, navigate) { navigateTo ->
                when (item.type) {
                  Type.APP_GRID -> AppsGridBundle(
                    bundle = item,
                    navigate = navigateTo,
                  )

                  Type.EDITORIAL -> EditorialBundle(
                    bundle = item,
                    navigate = navigateTo,
                  )

                  Type.CAROUSEL -> CarouselBundle(
                    bundle = item,
                    navigate = navigateTo,
                  )

                  Type.CAROUSEL_LARGE -> CarouselLargeBundle(
                    bundle = item,
                    navigate = navigateTo,
                  )

                  Type.CATEGORIES -> CategoriesBundle(
                    bundle = item,
                    navigate = navigateTo
                  )

                  Type.MY_GAMES -> MyGamesBundleView(
                    title = item.title.translateOrKeep(LocalContext.current),
                    icon = item.bundleIcon,
                    navigate = navigateTo,
                  )

                  Type.PUBLISHER_TAKEOVER -> PublisherTakeOverBundle(
                    bundle = item,
                    navigate = navigateTo,
                  )

                  else -> Unit
                }
              }
            }
          }
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
fun BundleHeader(
  title: String,
  icon: String?,
  hasMoreAction: Boolean,
  onClick: () -> Unit = {},
  titleColor: Color = Color.Unspecified,
  iconColor: Color? = null,
) {
  val label = stringResource(R.string.button_see_all_title)
  Row(
    modifier = Modifier
      .clearAndSetSemantics {
        heading()
        contentDescription = "$title bundle"
        if (hasMoreAction) {
          onClick(label = label) {
            onClick()
            true
          }
        }
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
      icon?.let {
        AptoideAsyncImage(
          modifier = Modifier
            .padding(end = 8.dp)
            .size(24.dp),
          data = icon,
          contentDescription = null,
        )
      }
      Text(
        text = title,
        maxLines = 2,
        modifier = Modifier
          .clearAndSetSemantics { },
        style = AGTypography.Title,
        color = titleColor,
        overflow = TextOverflow.Ellipsis
      )
    }
    if (hasMoreAction) {
      SeeMoreView(
        onClick = onClick,
        iconColor = iconColor,
      )
    }
  }
}

@Composable
fun SeeMoreView(
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
  iconColor: Color? = null,
) {
  Row(
    modifier = modifier
      .padding(start = 8.dp)
      .clickable {
        onClick()
      },
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = stringResource(R.string.button_see_all_title),
      modifier = Modifier.padding(end = 4.dp),
      style = AGTypography.InputsM,
      overflow = TextOverflow.Ellipsis,
      maxLines = 2,
    )
    Image(
      modifier = Modifier
        .size(24.dp)
        .requiredSize(24.dp),
      imageVector = getForward(iconColor ?: Palette.Primary),
      contentDescription = null,
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

  SwipeListener(interactionSource = pagerState.interactionSource)
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

@Composable
fun getSeeMoreRouteNavigation(
  bundle: Bundle,
  navigate: (String) -> Unit,
): () -> Unit {
  val analyticsContext = AnalyticsContext.current
  val genericAnalytics = rememberGenericAnalytics()

  val context = LocalContext.current
  val title = bundle.title.translateOrKeep(context)
  return {
    genericAnalytics.sendSeeAllClick(analyticsContext)
    navigate(
      buildSeeMoreRoute(title, "${bundle.tag}-more")
        .withBundleMeta(bundle.meta.copy(tag = "${bundle.tag}-more"))
    )
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
