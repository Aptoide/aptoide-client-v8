package com.aptoide.android.aptoidegames.feature_apps.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
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
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.domain.AppSource
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.previewAppsListIdleState
import cm.aptoide.pt.feature_apps.presentation.rememberAppsByTag
import cm.aptoide.pt.feature_home.domain.Bundle
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.SwipeListener
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIconRight
import com.aptoide.android.aptoidegames.feature_ad.MintegralAd
import com.aptoide.android.aptoidegames.feature_ad.MintegralAdEvent
import com.aptoide.android.aptoidegames.feature_ad.presentation.MintegralAdWrapper
import com.aptoide.android.aptoidegames.feature_ad.rememberMintegralAd
import com.aptoide.android.aptoidegames.home.BundleHeader
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.home.calculateLoopedBundleInitialItem
import com.aptoide.android.aptoidegames.home.floorMod
import com.aptoide.android.aptoidegames.home.getSeeMoreRouteNavigation
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.installer.presentation.ProgressText
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import kotlin.math.absoluteValue
import kotlin.math.sign

@Composable
fun EditorsChoiceBundle(
  bundle: Bundle,
  navigate: (String) -> Unit,
  spaceBy: Int = 0,
) {
  val (uiState, _) = rememberAppsByTag(bundle.tag, bundle.timestamp)

  Column {
    BundleHeader(
      title = bundle.title,
      icon = bundle.bundleIcon,
      hasMoreAction = bundle.hasMoreAction,
      onClick = getSeeMoreRouteNavigation(bundle = bundle, navigate = navigate),
      titleColor = Palette.White,
    )
    when (uiState) {
      is AppsListUiState.Idle -> {
        EditorsChoiceListView(
          appsList = uiState.apps,
          navigate = navigate
        )
        Spacer(Modifier.size(spaceBy.dp))
      }

      AppsListUiState.Empty,
      AppsListUiState.Error,
      AppsListUiState.NoConnection,
        -> Unit

      AppsListUiState.Loading -> {
        LoadingBundleView(height = 184.dp)
        Spacer(Modifier.size(spaceBy.dp))
      }
    }
  }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun EditorsChoiceListView(
  appsList: List<App>,
  navigate: (String) -> Unit,
) {
  var app: App? by remember { mutableStateOf(null) }
  val (ad, adEvents) = rememberMintegralAd()

  LaunchedEffect(ad) {
    ad?.let {
      app = it.app
    }
  }

  LaunchedEffect(adEvents) {
    adEvents.collect { event ->
      if (event is MintegralAdEvent.AdClick && app?.packageName == event.packageName) {
        navigate(
          buildAppViewRoute(
            if (app?.appId != null) {
              AppSource.of(app?.appId, null)
            } else {
              AppSource.of(null, event.packageName)
            }
          ).withItemPosition(0)
        )
      }
    }
  }

  val updatedList: List<App> = remember(ad) {
    ad?.let {
      appsList.toMutableList().apply {
        add(1, it.app)
      }.toImmutableList()
    } ?: appsList
  }

  EditorsChoiceCarouselList(
    appsList = updatedList,
    ad = ad,
    navigate = navigate
  )
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun EditorsChoiceCarouselList(
  appsList: List<App>,
  ad: MintegralAd? = null,
  navigate: (String) -> Unit,
) {
  val analyticsContext = AnalyticsContext.current
  val bundleAnalytics = rememberBundleAnalytics()

  val initialItem = remember(appsList) {
    calculateLoopedBundleInitialItem(appsList.size)
  }

  val pagerState = rememberPagerState(
    initialPage = initialItem,
    pageCount = { Int.MAX_VALUE }
  )

  val screenWidth = LocalConfiguration.current.screenWidthDp.dp
  val pageSize = (screenWidth - 32.dp)

  AutoScrollInfiniteHorizontalPager<App>(
    pagerState = pagerState,
    pageSize = PageSize.Fixed(pageSize),
    items = appsList,
    initialItem = initialItem,
    modifier = Modifier.fillMaxWidth(),
  ) { index ->
    val page = (index - 1).floorMod(appsList.size)
    val app = appsList[page]

    val appItemContent = @Composable {
      EditorsChoiceAppView(
        modifier = Modifier
          .graphicsLayer {
            val pageOffset =
              (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction

            lerp(
              start = 0.70f,
              stop = 1f,
              fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
            ).let { scale ->
              scaleY = scale
              scaleX = scale

              //Used to counteract spacing between items due to the scale effect.
              translationX = ((size.width - (size.width * scale)) / 2) * pageOffset.sign
            }

            lerp(
              start = 5f,
              stop = 0f,
              fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
            ).let { blur ->
              renderEffect = BlurEffect(blur, blur, TileMode.Decal)
            }
          },
        app = app,
        onClick = {
          bundleAnalytics.sendAppPromoClick(
            app = app,
            analyticsContext = analyticsContext.copy(itemPosition = page)
          )
          navigate(buildAppViewRoute(app).withItemPosition(page))
        },
        offset = {
          (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction
        }
      )
    }

    if (ad != null && page == 1) {
      MintegralAdWrapper(ad = ad) {
        appItemContent()
      }
    } else {
      appItemContent()
    }
  }
}

@Composable
private fun EditorsChoiceAppView(
  modifier: Modifier = Modifier,
  app: App,
  onClick: (() -> Unit)?,
  offset: () -> Float = { 0f },
) {
  val clickableModifier = if (onClick != null) {
    Modifier.clickable(onClick = onClick)
  } else {
    Modifier
  }

  Column(
    modifier = modifier
      .semantics(mergeDescendants = true) {
        contentDescription = app.name
      }
      .then(clickableModifier)
  ) {
    Box(
      contentAlignment = Alignment.TopEnd,
    ) {
      AptoideFeatureGraphicImage(
        modifier = Modifier.aspectRatio(328f / 160f),
        data = app.featureGraphic,
        contentDescription = null,
      )
      if (app.isAppCoins) {
        Image(
          imageVector = getBonusIconRight(
            iconColor = Palette.Primary,
            outlineColor = Palette.Black,
            backgroundColor = Palette.Secondary
          ),
          contentDescription = null,
          modifier = Modifier.size(32.dp),
        )
      }
    }
    Row {
      AppIconWProgress(
        app = app,
        contentDescription = null,
        modifier = Modifier
          .graphicsLayer {
            alpha = lerp(
              start = 0f,
              stop = 1f,
              fraction = 1f - offset().absoluteValue.coerceIn(0f, 1f)
            )

            //Translation and alpha effects need to be done at the same level to avoid visual bugs.
            //Therefore, the alpha can not be applied directly to the parent composable.
            translationY = with(density) { (-16).dp.toPx() }
          }
          .padding(start = 8.dp)
          .size(55.dp),
      )
      Row(
        modifier = Modifier
          .padding(top = 8.dp)
          .graphicsLayer {
            alpha = lerp(
              start = 0f,
              stop = 1f,
              fraction = 1f - offset().absoluteValue.coerceIn(0f, 1f)
            )
          },
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(
          modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
            .weight(1f),
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = app.name,
            modifier = Modifier
              .clearAndSetSemantics {}
              .wrapContentHeight(unbounded = true),
            color = Palette.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = AGTypography.DescriptionGames
          )
          ProgressText(
            modifier = Modifier.wrapContentHeight(unbounded = true, align = Alignment.Top),
            app = app,
            showVersionName = false
          )
        }
        InstallViewShort(app = app)
      }
    }
  }
}

@Composable
fun <T> AutoScrollInfiniteHorizontalPager(
  modifier: Modifier = Modifier,
  pagerState: PagerState,
  items: List<T>,
  initialItem: Int = calculateLoopedBundleInitialItem(items.size),
  pageSize: PageSize = PageSize.Fill,
  content: @Composable (page: Int) -> Unit,
) {
  val scope = rememberCoroutineScope()
  val autoScrollViewModel = perCarouselViewModel(
    carouselTag = items.hashCode().toString(),
  )
  val currentPage by autoScrollViewModel.uiState.collectAsState()

  LaunchedEffect(initialItem) {
    pagerState.scrollToPage(
      page = initialItem,
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
    snapPosition = SnapPosition.Center,
    pageSize = pageSize,
    state = pagerState,
    beyondViewportPageCount = 1,
    modifier = modifier.semantics {
      collectionInfo = CollectionInfo(1, items.size)
    }
  ) { index ->
    content(index)
  }
}

@PreviewDark
@Composable
private fun EditorsChoiceListViewPreview() {
  AptoideTheme {
    EditorsChoiceListView(
      appsList = previewAppsListIdleState.apps,
      navigate = {}
    )
  }
}
