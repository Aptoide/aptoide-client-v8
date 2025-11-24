package com.aptoide.android.aptoidegames.home

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri.encode
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.Type
import cm.aptoide.pt.feature_home.presentation.BundlesViewUiState
import cm.aptoide.pt.feature_home.presentation.BundlesViewUiStateType
import cm.aptoide.pt.feature_home.presentation.bundlesList
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.Platform.isHmd
import com.aptoide.android.aptoidegames.Platform.isHmdDevice
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsBundleMeta
import com.aptoide.android.aptoidegames.analytics.presentation.SwipeListener
import com.aptoide.android.aptoidegames.analytics.presentation.withBundleMeta
import com.aptoide.android.aptoidegames.categories.presentation.CategoriesBundle
import com.aptoide.android.aptoidegames.design_system.IndeterminateCircularLoading
import com.aptoide.android.aptoidegames.drawables.icons.getForward
import com.aptoide.android.aptoidegames.editorial.EditorialBundle
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.error_views.NoConnectionView
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppsGridBundle
import com.aptoide.android.aptoidegames.feature_apps.presentation.BonusSectionView
import com.aptoide.android.aptoidegames.feature_apps.presentation.CarouselBundle
import com.aptoide.android.aptoidegames.feature_apps.presentation.CarouselLargeBundle
import com.aptoide.android.aptoidegames.feature_apps.presentation.DEFAULT_AUTO_SCROLL_SPEED
import com.aptoide.android.aptoidegames.feature_apps.presentation.EditorsChoiceBundle
import com.aptoide.android.aptoidegames.feature_apps.presentation.MyGamesBundleView
import com.aptoide.android.aptoidegames.feature_apps.presentation.PublisherTakeOverBundle
import com.aptoide.android.aptoidegames.feature_apps.presentation.buildSeeMoreRoute
import com.aptoide.android.aptoidegames.feature_apps.presentation.perCarouselViewModel
import com.aptoide.android.aptoidegames.feature_apps.presentation.rememberBundleAnalytics
import com.aptoide.android.aptoidegames.feature_promotional.AppComingSoonPromotionalView
import com.aptoide.android.aptoidegames.feature_promotional.EventPromotionalView
import com.aptoide.android.aptoidegames.feature_promotional.NewAppPromotionalView
import com.aptoide.android.aptoidegames.feature_promotional.NewAppVersionPromotionalView
import com.aptoide.android.aptoidegames.feature_promotional.NewsPromotionalView
import com.aptoide.android.aptoidegames.feature_rtb.presentation.RTBSectionView
import com.aptoide.android.aptoidegames.gamesfeed.presentation.GamesFeedBundle
import com.aptoide.android.aptoidegames.gamesfeed.presentation.rememberGamesFeedVisibility
import com.aptoide.android.aptoidegames.home.analytics.meta
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.PaEBundleView
import com.aptoide.android.aptoidegames.play_and_earn.presentation.home.rememberPaEHeaderState
import com.aptoide.android.aptoidegames.play_and_earn.rememberShouldShowPlayAndEarn
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.sign

val translatedTitles = mapOf(
  "Just Arrived" to R.string.fixed_bundle_just_arrived_title,
  "My Games" to R.string.fixed_bundle_my_games_title,
  "Editors' Choice" to R.string.fixed_bundle_editors_choice_title,
  "Editorial" to R.string.fixed_bundle_editorial_title,
  "Trending" to R.string.fixed_bundle_trending_title,
  "Categories" to R.string.categories,
  "Highlighted" to R.string.fixed_bundle_highlighted
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BundlesScreen(
  navigate: (String) -> Unit,
) {
  val (viewState, loadFreshHomeBundles) = bundlesList()
  val shouldShowGamesFeed = rememberGamesFeedVisibility()
  var shouldShowLoadingView by remember { mutableStateOf(false) }

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
      when {
        shouldShowLoadingView || viewState.type == BundlesViewUiStateType.LOADING || viewState.type == BundlesViewUiStateType.RELOADING -> LoadingView()

        viewState.type == BundlesViewUiStateType.NO_CONNECTION -> NoConnectionView(
          onRetryClick = {
            loadFreshHomeBundles()
          }
        )

        viewState.type == BundlesViewUiStateType.ERROR -> GenericErrorView(
          onRetryClick = loadFreshHomeBundles
        )

        viewState.type == BundlesViewUiStateType.IDLE -> {
          val filteredBundles = remember(viewState.bundles, shouldShowGamesFeed) {
            val bundles = if (shouldShowGamesFeed == true) {
              viewState.bundles.injectGamesFeed()
            } else {
              viewState.bundles
            }

            viewState.copy(bundles = bundles).filterHMD().bundles
          }

          BundlesView(
            viewState = viewState.copy(bundles = filteredBundles).injectPaEBundle(),
            navigate = navigate,
            onShowLoading = { showLoading ->
              shouldShowLoadingView = showLoading
            }
          )
        }
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
  onShowLoading: (Boolean) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.TopCenter)
      .padding(top = 16.dp)
  ) {
    LazyColumn(
      state = rememberBottomBarMenuScrollState(state = rememberLazyListState(), route = gamesRoute),
      modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.TopCenter),
      contentPadding = PaddingValues(bottom = 72.dp)
    ) {
      items(viewState.bundles) { bundle ->
        OverrideAnalyticsBundleMeta(bundle.meta, navigate) { navigateTo ->
          when (bundle.type) {
            Type.APP_GRID -> AppsGridBundle(
              bundle = bundle,
              navigate = navigateTo,
              spaceBy = 32
            )

            Type.EDITORIAL -> EditorialBundle(
              bundle = bundle,
              navigate = navigateTo,
              spaceBy = 32
            )

            Type.CAROUSEL -> {
              if (bundle.tag == "apps-group-editors-choice") {
                EditorsChoiceBundle(
                  bundle = bundle,
                  navigate = navigateTo,
                  spaceBy = 32
                )
              } else {
                CarouselBundle(
                  bundle = bundle,
                  navigate = navigateTo,
                  spaceBy = 32
                )
              }
            }

            Type.CAROUSEL_LARGE -> CarouselLargeBundle(
              bundle = bundle,
              navigate = navigateTo,
              spaceBy = 32
            )

            Type.CATEGORIES -> CategoriesBundle(
              bundle = bundle,
              navigate = navigateTo,
              spaceBy = 32
            )

            Type.MY_GAMES -> MyGamesBundleView(
              title = bundle.title.translateOrKeep(LocalContext.current),
              icon = bundle.bundleIcon,
              navigate = navigateTo,
              spaceBy = 32
            )

            Type.PUBLISHER_TAKEOVER -> PublisherTakeOverBundle(
              bundle = bundle,
              navigate = navigateTo,
              spaceBy = 32
            )

            Type.GAMES_FEED -> GamesFeedBundle(
              bundle = bundle,
              navigate = navigateTo,
              spaceBy = 32
            )

            Type.APPC_BANNER -> BonusSectionView(
              bundle = bundle,
              navigate = navigateTo,
              spaceBy = 32
            )

            Type.NEW_APP -> NewAppPromotionalView(
              bundle = bundle,
              navigate = navigateTo,
              spaceBy = 32
            )

            Type.NEW_APP_VERSION -> NewAppVersionPromotionalView(
              bundle = bundle,
              navigate = navigateTo,
              spaceBy = 32
            )

            Type.NEWS_ITEM -> NewsPromotionalView(
              bundle = bundle,
              navigate = navigateTo,
              spaceBy = 32
            )

            Type.IN_GAME_EVENT -> EventPromotionalView(
              bundle = bundle,
              navigate = navigateTo,
              spaceBy = 32
            )

            Type.APP_COMING_SOON -> AppComingSoonPromotionalView(
              bundle = bundle,
              spaceBy = 32
            )

            Type.RTB_PROMO -> RTBSectionView(
              bundle = bundle.copy(title = "Highlighted".translateOrKeep(LocalContext.current)),
              navigate = navigateTo,
              spaceBy = 32,
              onShowLoading = onShowLoading
            )

            Type.PLAY_AND_EARN -> PaEBundleView(
              navigate = navigateTo,
              spaceBy = 32
            )

            else -> Unit
          }
        }
      }
    }
  }
}

@Composable
fun LoadingBundleView(height: Dp) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(height),
    contentAlignment = Alignment.Center,
  ) {
    IndeterminateCircularLoading(color = Palette.Primary)
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
      .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
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
        text = title.translateOrKeep(LocalContext.current),
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
        .requiredSize(24.dp)
        .padding(all = 3.dp),
      imageVector = getForward(iconColor ?: Palette.Primary),
      contentDescription = null,
    )
  }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun HorizontalPagerView(
  appsList: List<App>,
  modifier: Modifier = Modifier,
  scrollSpeedInSeconds: Long = DEFAULT_AUTO_SCROLL_SPEED,
  content: @Composable (modifier: Modifier, page: Int, app: App, isCurrentPage: Boolean) -> Unit,
) {
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp
  val padding = (screenWidth - 280.dp - 16.dp) / 2 + 8.dp
  val contentPadding = PaddingValues(
    start = padding,
    end = padding
  )

  val initialItem = remember(appsList) {
    calculateLoopedBundleInitialItem(appsList.size)
  }

  val pagerState = rememberPagerState(
    initialPage = initialItem,
    pageCount = { Int.MAX_VALUE }
  )
  val scope = rememberCoroutineScope()

  val autoScrollViewModel = perCarouselViewModel(
    carouselTag = appsList.hashCode().toString(),
    scrollSpeedInSeconds = scrollSpeedInSeconds.takeIf { it > 0L } ?: DEFAULT_AUTO_SCROLL_SPEED
  )
  val currentPage by autoScrollViewModel.uiState.collectAsState()

  LaunchedEffect(initialItem) {
    pagerState.animateScrollToPage(
      page = initialItem,
      animationSpec = tween(durationMillis = 500)
    )
  }

  /*Used when the current page state is changed in the AutoScrollViewModel*/
  LaunchedEffect(currentPage) {
    currentPage?.let {
      scope.launch {
        pagerState.animateScrollToPage(
          page = it,
          animationSpec = tween(durationMillis = 500)
        )
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
          )

        lerp(
          start = 0.87f,
          stop = 1f,
          fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
        ).also { scale ->
          scaleY = scale
          scaleX = scale

          translationX = ((size.width - (size.width * scale)) / 2) * pageOffset.sign
        }
      }
    content(
      pageModifier,
      page,
      app,
      page == pagerState.currentPage.minus(1).floorMod(appsList.size)
    )
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
    IndeterminateCircularLoading(color = Palette.Primary)
  }
}

@Composable
fun getSeeMoreRouteNavigation(
  bundle: Bundle,
  navigate: (String) -> Unit,
): () -> Unit {
  val analyticsContext = AnalyticsContext.current
  val bundleAnalytics = rememberBundleAnalytics()

  val context = LocalContext.current
  val title = encode(bundle.title.translateOrKeep(context))
  return {
    bundleAnalytics.sendSeeAllClick(analyticsContext)
    navigate(
      buildSeeMoreRoute(title, "${bundle.tag}-more")
        .withBundleMeta(bundle.meta.copy(tag = "${bundle.tag}-more"))
    )
  }
}

fun calculateLoopedBundleInitialItem(appsListSize: Int): Int {
  val numberOfLists: Int = Int.MAX_VALUE / appsListSize
  val middleList: Int = numberOfLists / 2
  val middleListLastIndex: Int = middleList * appsListSize
  return (middleListLastIndex - appsListSize) + 1
}

fun Int.floorMod(other: Int): Int = when (other) {
  0 -> this
  else -> this - floorDiv(other) * other
}

private fun BundlesViewUiState.filterHMD(): BundlesViewUiState {
  return if (isHmd && isHmdDevice) {
    this
  } else {
    this.copy(bundles = bundles.filter { it.tag != "apps-group-hmd-controller" })
  }
}

fun List<Bundle>.injectGamesFeed(): List<Bundle> {
  val gamesFeedBundle = Bundle(
    title = "Roblox",
    actions = emptyList(),
    type = Type.GAMES_FEED,
    tag = "games-feed",
    bundleIcon = null,
    background = null,
    view = null
  )

  return toMutableList().apply {
    val insertPosition = if (size >= 1) 1 else 0
    add(insertPosition, gamesFeedBundle)
  }
}

@Composable
private fun BundlesViewUiState.injectPaEBundle(): BundlesViewUiState {
  val shouldShowPlayAndEarn = rememberShouldShowPlayAndEarn()
  val (hasShownHeader, _) = rememberPaEHeaderState()

  if (shouldShowPlayAndEarn && hasShownHeader == true) {
    val paeBundle = Bundle(
      title = stringResource(R.string.play_and_earn_title),
      actions = emptyList(),
      type = Type.PLAY_AND_EARN,
      tag = "play-and-earn-injected-bundle"
    )

    return copy(
      bundles = bundles.toMutableList().apply {
        if (bundles.size >= 3) {
          add(2, paeBundle)
        } else {
          add(paeBundle)
        }
      }
    )
  } else {
    return this
  }
}

fun String.translateOrKeep(localContext: Context): String {
  return translatedTitles[this]
    ?.let { localContext.getString(it) }
    ?: this
}
