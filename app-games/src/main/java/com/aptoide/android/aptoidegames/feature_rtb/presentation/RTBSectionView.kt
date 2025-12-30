package com.aptoide.android.aptoidegames.feature_rtb.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_campaigns.AptoideMMPCampaign
import cm.aptoide.pt.feature_campaigns.UTMInfo
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.randomBundle
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.feature_rtb.data.RTBApp
import com.aptoide.android.aptoidegames.feature_rtb.data.RTBAppsListUiState
import com.aptoide.android.aptoidegames.home.BundleHeader
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.theme.AptoideTheme

private var hasSentImpression = false

@Composable
fun RTBAptoideMMPController(
  apps: List<RTBApp>,
  bundleTag: String,
) {
  apps.forEachIndexed { index, rtbApp ->
    val app = rtbApp.app
    if (!hasSentImpression) {
      app.campaigns?.toAptoideMMPCampaign()
        ?.sendImpressionEvent(bundleTag, app.packageName)
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
  onShowLoading: (Boolean) -> Unit = {}
) {
  val (uiState, _) = rememberRTBApps(bundle.tag, bundle.timestamp)

  when (uiState) {
    is RTBAppsListUiState.Idle -> {
      Column {
        BundleHeader(
          title = bundle.title,
          icon = bundle.bundleIcon,
          hasMoreAction = true,
          onClick =
            getRTBMoreRouteNavigation(
              bundle = bundle,
              navigate = navigate
            )
        )
        RTBBundleView(
          bundle = bundle,
          navigate = navigate,
          apps = uiState.apps,
          onShowLoading = onShowLoading
        )
        Spacer(Modifier.size(spaceBy.dp))
      }
    }

    RTBAppsListUiState.Empty,
    RTBAppsListUiState.Error,
    RTBAppsListUiState.NoConnection,
      -> Unit

    RTBAppsListUiState.Loading -> {
      LoadingBundleView(height = 184.dp)
      Spacer(Modifier.size(spaceBy.dp))
    }
  }

  LaunchedEffect(Unit) {
    if (!AptoideMMPCampaign.allowedBundleTags.keys.contains(bundle.tag)) {
      AptoideMMPCampaign.allowedBundleTags[bundle.tag] = UTMInfo(
        utmMedium = "rtb",
        utmCampaign = "regular",
        utmContent = "home-bundle"
      )
    }
  }
}

@Composable
fun RTBBundleView(
  bundle: Bundle,
  navigate: (String) -> Unit,
  apps: List<RTBApp>,
  onShowLoading: (Boolean) -> Unit = {}
) {
  val homeApps = apps.take(9)
  RTBAptoideMMPController(homeApps, bundle.tag)
  RTBAppsRowView(
    rtbAppsList = homeApps,
    navigate = navigate,
    onShowLoading = onShowLoading
  )
}

@PreviewDark
@Composable
private fun RealBonusBundlePreview() {
  AptoideTheme {
    RTBSectionView(
      bundle = randomBundle,
      navigate = {},
      onShowLoading = {}
    )
  }
}

fun AnalyticsUIContext.isRTB() =
  this.bundleMeta?.tag?.lowercase()?.contains("rtb") ?: false
