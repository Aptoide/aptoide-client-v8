package com.aptoide.android.aptoidegames.feature_rtb.presentation

import android.net.Uri.encode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_campaigns.AptoideMMPCampaign
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.randomBundle
import com.aptoide.android.aptoidegames.analytics.presentation.withBundleMeta
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppsRowView
import com.aptoide.android.aptoidegames.feature_apps.presentation.BonusSectionGeneralizedView
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.home.analytics.meta
import com.aptoide.android.aptoidegames.theme.AptoideTheme

private var hasSentImpression = false

@Composable
fun RTBAptoideMMPController(
  apps: List<App>,
  bundleTag: String,
  placement: String,
) {
  apps.forEachIndexed { index, rtbApp ->
    if (!hasSentImpression) {
      rtbApp.campaigns?.toAptoideMMPCampaign()
        ?.sendImpressionEvent(bundleTag, rtbApp.packageName)
      rtbApp.campaigns?.run {
        placementType = placement
      }
      if (index == apps.size - 1) {
        hasSentImpression = true
      }
    }
  }
}

@Composable
fun RTBSectionView(
  bundle: Bundle,
  navigate: (String) -> Unit,
  spaceBy: Int = 0,
) {
  val (uiState, _) = rememberRTBApps(bundle.tag, bundle.timestamp)

  when (uiState) {
    is AppsListUiState.Idle -> {

      BonusSectionGeneralizedView(
        onHeaderClick = getRTBMoreRouteNavigation(
          bundle = bundle,
          navigate = navigate
        ),
        spaceBy = spaceBy,
        showMoreButton = true
      ) {
        RTBBundleView(
          bundle = bundle,
          navigate = navigate,
          apps = uiState.apps
        )
      }
    }

    AppsListUiState.Empty,
    AppsListUiState.Error,
    AppsListUiState.NoConnection,
      -> Unit

    AppsListUiState.Loading -> LoadingBundleView(height = 184.dp)
  }

  LaunchedEffect(Unit) {
    if (!AptoideMMPCampaign.allowedBundleTags.keys.contains(bundle.tag)) {
      AptoideMMPCampaign.allowedBundleTags[bundle.tag] = "ag-rtb" to ("ag-rtb-${bundle.tag}")
    }
  }
}

@Composable
fun RTBBundleView(
  bundle: Bundle,
  navigate: (String) -> Unit,
  apps: List<App>,
) {
  val homeApps = apps.take(9)
  RTBAptoideMMPController(homeApps, bundle.tag, "home-bundle")
  AppsRowView(
    appsList = homeApps,
    navigate = navigate,
  )
}

@Composable
fun getRTBMoreRouteNavigation(
  bundle: Bundle,
  navigate: (String) -> Unit,
): () -> Unit = {
  navigate(
    buildRtbSeeMoreRoute(encode(bundle.title), "${bundle.tag}-more")
      .withBundleMeta(bundle.meta.copy(tag = "${bundle.tag}-more"))
  )
}

@PreviewDark
@Composable
private fun RealBonusBundlePreview() {
  AptoideTheme {
    RTBSectionView(
      bundle = randomBundle,
      navigate = {}
    )
  }
}
