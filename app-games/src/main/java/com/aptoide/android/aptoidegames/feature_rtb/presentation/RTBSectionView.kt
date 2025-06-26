package com.aptoide.android.aptoidegames.feature_rtb.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_campaigns.AptoideMMPCampaign
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.randomBundle
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppsRowView
import com.aptoide.android.aptoidegames.feature_apps.presentation.BonusSectionGeneralizedView
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.theme.AptoideTheme

internal var hasSentImpression = false

@Composable
private fun RTBAptoideMMPController(
  appsListUiState: AppsListUiState,
  bundleTag: String,
  placement: String,
) {
  when (appsListUiState) {
    is AppsListUiState.Idle ->
      appsListUiState.apps.forEachIndexed { index, rtbApp ->
        if (!hasSentImpression) {
          rtbApp.campaigns?.toAptoideMMPCampaign()
            ?.sendImpressionEvent(bundleTag, rtbApp.packageName)
          rtbApp.campaigns?.run {
            placementType = placement
          }
          if (index == appsListUiState.apps.size - 1) {
            hasSentImpression = true
          }
        }
      }

    else -> {}
  }
}

@Composable
fun RTBSectionView(
  bundle: Bundle,
  navigate: (String) -> Unit,
  spaceBy: Int = 0,
) {
  LaunchedEffect(Unit) {
    if (!AptoideMMPCampaign.allowedBundleTags.keys.contains(bundle.tag)) {
      AptoideMMPCampaign.allowedBundleTags[bundle.tag] = "ag-rtb" to ("ag-rtb-${bundle.tag}")
    }
  }
  BonusSectionGeneralizedView(
    onHeaderClick = {},
    spaceBy = spaceBy,
    showMoreButton = false
  ) {
    RTBBundleView(
      bundle = bundle,
      navigate = navigate
    )
  }
}

@Composable
fun RTBBundleView(
  bundle: Bundle,
  navigate: (String) -> Unit
) {

  val (uiState, _) = rememberRTBApps(bundle.tag, bundle.timestamp)
  RTBAptoideMMPController(uiState, bundle.tag, "home-bundle")
  when (uiState) {
    is AppsListUiState.Idle -> AppsRowView(
      appsList = uiState.apps,
      navigate = navigate,
    )

    AppsListUiState.Empty,
    AppsListUiState.Error,
    AppsListUiState.NoConnection,
      -> Unit

    AppsListUiState.Loading -> LoadingBundleView(height = 184.dp)
  }
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
