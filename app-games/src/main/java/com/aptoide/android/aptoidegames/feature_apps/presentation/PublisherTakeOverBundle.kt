package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.rememberAppsByTag
import cm.aptoide.pt.feature_campaigns.AptoideMMPCampaign
import cm.aptoide.pt.feature_campaigns.UTMInfo
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.randomBundle
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIconRight
import com.aptoide.android.aptoidegames.home.HorizontalPagerView
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.home.translateOrKeep
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.installer.presentation.ProgressText
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random
import kotlin.random.nextInt

internal var hasSentImpression = false

@Composable
fun AptoideMMPController(
  appsListUiState: AppsListUiState,
  bundleTag: String,
  placement: String,
) {
  when (appsListUiState) {
    is AppsListUiState.Idle ->
      appsListUiState.apps.onEach {
        if (it.isAppCoins && !hasSentImpression) {
          it.campaigns?.toAptoideMMPCampaign()
            ?.sendImpressionEvent(bundleTag, BuildConfig.APPLICATION_ID)
          hasSentImpression = true
        }
        it.campaigns?.run {
          placementType = placement
        }
      }

    else -> {}
  }
}

@Composable
fun PublisherTakeOverBundle(
  bundle: Bundle,
  navigate: (String) -> Unit,
  spaceBy: Int = 0,
) {
  val (uiState, _) = rememberAppsByTag(bundle.tag, bundle.timestamp)
  val (bottomUiState, _) = rememberAppsByTag(bundle.bottomTag ?: "", bundle.timestamp)

  LaunchedEffect(Unit) {
    if (!AptoideMMPCampaign.allowedBundleTags.keys.contains(bundle.tag)) {
      AptoideMMPCampaign.allowedBundleTags[bundle.tag] = UTMInfo(
        utmMedium = "PTO",
        utmCampaign = "pto-${bundle.tag}",
        utmContent = "home-pto"
      )
      AptoideMMPCampaign.allowedBundleTags["${bundle.tag}-more"] = UTMInfo(
        utmMedium = "PTO",
        utmCampaign = "pto-${bundle.tag}",
        utmContent = "pto-seeall"
      )
    }
  }

  AptoideMMPController(
    appsListUiState = uiState, bundleTag = bundle.tag, placement = "app_1st_line"
  )
  AptoideMMPController(
    appsListUiState = bottomUiState, bundleTag = bundle.tag, placement = "app_2nd_line"
  )

  PublisherTakeOverContent(
    bundle = bundle,
    uiState = uiState,
    bottomUiState = bottomUiState,
    navigate = navigate,
    spaceBy = spaceBy
  )
}

@Composable
fun PublisherTakeOverContent(
  bundle: Bundle,
  uiState: AppsListUiState,
  bottomUiState: AppsListUiState,
  navigate: (String) -> Unit,
  spaceBy: Int = 0
) {
  Column {
    Box {
      AptoideAsyncImage(
        modifier = Modifier.matchParentSize(),
        data = bundle.background,
        contentDescription = null
      )
      Column(
        modifier = Modifier
          .background(color = Palette.Black.copy(0.7f))
          .padding(bottom = 24.dp)
      ) {
        Row(
          modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 24.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          val textStyle = bundle.bundleIcon?.let { AGTypography.InputsL } ?: AGTypography.Title
          bundle.bundleIcon?.let {
            AptoideAsyncImage(
              modifier = Modifier
                .size(40.dp)
                .background(color = Color.Transparent),
              data = it,
              contentDescription = null,
            )
          }
          Text(
            text = bundle.title.translateOrKeep(LocalContext.current),
            modifier = Modifier.semantics { heading() },
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            color = Palette.White,
            style = textStyle
          )
        }

        when (uiState) {
          is AppsListUiState.Idle -> PublisherTakeOverListView(
            bundleTag = bundle.tag,
            appsList = uiState.apps,
            navigate = navigate,
          )

          AppsListUiState.Empty,
          AppsListUiState.Error,
          AppsListUiState.NoConnection,
            -> { /*nothing to show*/
          }

          AppsListUiState.Loading -> LoadingBundleView(height = 184.dp)
        }
        when (bottomUiState) {
          is AppsListUiState.Idle -> {
            AppsRowView(
              appsList = bottomUiState.apps,
              navigate = navigate,
            )
            Spacer(Modifier.size(spaceBy.dp))
          }

          AppsListUiState.Empty,
          AppsListUiState.Error,
          AppsListUiState.NoConnection,
            -> { /*nothing to show*/
          }

          AppsListUiState.Loading -> {
            LoadingBundleView(height = 184.dp)
            Spacer(Modifier.size(spaceBy.dp))
          }
        }
      }
    }
  }
}

@Composable
fun PublisherTakeOverListView(
  bundleTag: String,
  appsList: List<App>,
  navigate: (String) -> Unit,
) {
  val analyticsContext = AnalyticsContext.current
  val bundleAnalytics = rememberBundleAnalytics()

  HorizontalPagerView(
    appsList = appsList,
    modifier = Modifier.padding(bottom = 24.dp)
  ) { modifier, page, app, _ ->

    Box(
      modifier
        .width(280.dp)
        .height(184.dp)
        .background(color = Color.Transparent)
    ) {
      Column(
        modifier = Modifier
          .semantics(mergeDescendants = true) { }
          .clickable(onClick = {
            app.campaigns
              ?.toAptoideMMPCampaign()
              ?.sendClickEvent(bundleTag)
            bundleAnalytics.sendAppPromoClick(
              app = app,
              analyticsContext = analyticsContext.copy(itemPosition = page)
            )
            navigate(
              buildAppViewRoute(app)
                .withItemPosition(page)
            )
          }
          )
      ) {
        Box(
          contentAlignment = Alignment.TopEnd
        ) {
          AptoideFeatureGraphicImage(
            modifier = Modifier
              .padding(bottom = 8.dp)
              .width(280.dp)
              .height(136.dp),
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
        Row(
          horizontalArrangement = Arrangement.SpaceBetween,
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
              modifier = Modifier.wrapContentHeight(unbounded = true),
              maxLines = 1,
              color = Palette.White,
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
}

@PreviewDark
@Composable
fun PublisherTakeOverBundleContentPreview(
  @PreviewParameter(
    PublisherTakeoverUiStateProvider::class
  ) uiState: Pair<AppsListUiState, AppsListUiState>,
) {
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    PublisherTakeOverContent(
      bundle = randomBundle,
      uiState = uiState.first,
      bottomUiState = uiState.second,
      navigate = {},
    )
  }
}

class PublisherTakeoverUiStateProvider :
  PreviewParameterProvider<Pair<AppsListUiState, AppsListUiState>> {
  override val values: Sequence<Pair<AppsListUiState, AppsListUiState>> =
    listOf(
      AppsListUiState.Idle(List(size = Random.nextInt(1..10)) { randomApp }),
      AppsListUiState.Loading,
      AppsListUiState.Empty,
      AppsListUiState.NoConnection,
      AppsListUiState.Error
    ).let {
      sequence {
        it.forEach { a ->
          it.forEach { b ->
            yield(a to b)
          }
        }
      }
    }
}
