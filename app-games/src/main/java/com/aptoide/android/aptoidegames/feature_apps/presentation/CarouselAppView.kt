package com.aptoide.android.aptoidegames.feature_apps.presentation

import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.extractVideoId
import cm.aptoide.pt.extensions.isYoutubeURL
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.domain.AppSource
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.previewAppsListIdleState
import cm.aptoide.pt.feature_apps.presentation.rememberAppsByTag
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.randomBundle
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIconRight
import com.aptoide.android.aptoidegames.feature_ad.MintegralAd
import com.aptoide.android.aptoidegames.feature_ad.MintegralAdEvent
import com.aptoide.android.aptoidegames.feature_ad.rememberMintegralAd
import com.aptoide.android.aptoidegames.home.BundleHeader
import com.aptoide.android.aptoidegames.home.HorizontalPagerView
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.home.getSeeMoreRouteNavigation
import com.aptoide.android.aptoidegames.home.rememberShouldShowVideos
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.installer.presentation.ProgressText
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.videos.presentation.CarouselAppYoutubePlayer
import okhttp3.internal.toImmutableList

@Composable
fun CarouselBundle(
  bundle: Bundle,
  navigate: (String) -> Unit,
) {
  val (uiState, _) = rememberAppsByTag(bundle.tag, bundle.timestamp)

  RealCarouselBundle(
    bundle = bundle,
    uiState = uiState,
    navigate = navigate
  )
}

@Composable
private fun RealCarouselBundle(
  bundle: Bundle,
  uiState: AppsListUiState,
  navigate: (String) -> Unit,
) {
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
        CarouselListView(
          appsList = uiState.apps,
          bundleTag = bundle.tag,
          navigate = navigate
        )
      }

      AppsListUiState.Empty,
      AppsListUiState.Error,
      AppsListUiState.NoConnection,
        -> SmallEmptyView(modifier = Modifier.height(184.dp))

      AppsListUiState.Loading -> LoadingBundleView(height = 184.dp)
    }
  }
}

@Composable
private fun CarouselListView(
  appsList: List<App>,
  bundleTag: String,
  navigate: (String) -> Unit,
) {
  val analyticsContext = AnalyticsContext.current
  val bundleAnalytics = rememberBundleAnalytics()
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

  val showVideos = rememberShouldShowVideos(bundleTag)
  HorizontalPagerView(
    appsList = updatedList,
    scrollSpeedInSeconds = if (showVideos) 9L else DEFAULT_AUTO_SCROLL_SPEED
  ) { modifier, page, item, isCurrentPage ->
    Box(
      modifier
        .width(280.dp)
        .background(color = Color.Transparent)
    ) {
      if (ad != null && page == 1) {
        MintegralNativeAdView(
          ad = ad,
          item = item,
          showVideos = showVideos,
          isCurrentPage = isCurrentPage
        )
      } else {
        CarouselAppView(
          app = item,
          showVideo = showVideos && isCurrentPage,
          onClick = {
            bundleAnalytics.sendAppPromoClick(
              app = item,
              analyticsContext = analyticsContext.copy(itemPosition = page)
            )
            navigate(
              buildAppViewRoute(item)
                .withItemPosition(page)
            )
          }
        )
      }
    }
  }
}

@Composable
fun MintegralNativeAdView(
  ad: MintegralAd,
  item: App,
  showVideos: Boolean,
  isCurrentPage: Boolean,
) {
  AndroidView(
    factory = { context ->
      val container = FrameLayout(context).apply {
        layoutParams = LayoutParams(
          LayoutParams.MATCH_PARENT,
          LayoutParams.WRAP_CONTENT
        )
      }

      val composeView = ComposeView(context).apply {
        setContent {
          CarouselAppView(
            app = item,
            showVideo = showVideos && isCurrentPage,
            onClick = null
          )
        }
      }
      container.addView(
        composeView,
        FrameLayout.LayoutParams(
          FrameLayout.LayoutParams.MATCH_PARENT,
          FrameLayout.LayoutParams.WRAP_CONTENT
        )
      )
      ad.register(container)

      container
    }
  )
}

@Composable
private fun CarouselAppView(
  app: App,
  showVideo: Boolean,
  onClick: (() -> Unit)?,
) {
  val clickableModifier = if (onClick != null) {
    Modifier.clickable(onClick = onClick)
  } else {
    Modifier
  }
  val videoId = remember(app) {
    app.videos.getOrNull(0)
      ?.takeIf { it.isNotEmpty() && it.isYoutubeURL() }
      ?.extractVideoId()
  }
  Column(
    modifier = Modifier
      .requiredWidth(280.dp)
      .semantics(mergeDescendants = true) {
        contentDescription = app.name
      }
      .then(clickableModifier)
  ) {
    Box(
      contentAlignment = Alignment.TopEnd
    ) {
      if (showVideo && videoId != null) {
        CarouselAppYoutubePlayer(
          modifier = Modifier
            .width(280.dp)
            .height(157.dp),
          videoId = videoId,
          onErrorContent = {
            AptoideFeatureGraphicImage(
              modifier = Modifier
                .width(280.dp)
                .height(157.dp),
              data = app.featureGraphic,
              contentDescription = null,
            )
          }
        )
        Box(
          modifier = Modifier
            .matchParentSize()
            .then(clickableModifier)
        )
      } else {
        AptoideFeatureGraphicImage(
          modifier = Modifier
            .width(280.dp)
            .height(136.dp),
          data = app.featureGraphic,
          contentDescription = null,
        )
      }
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
    Row(
      modifier = Modifier.padding(top = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      AppIconWProgress(
        app = app,
        contentDescription = null,
        modifier = Modifier.size(40.dp),
      )
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

@PreviewDark
@Composable
private fun RealCarouselBundlePreview() {
  AptoideTheme {
    RealCarouselBundle(
      bundle = randomBundle,
      uiState = previewAppsListIdleState,
      navigate = {}
    )
  }
}
