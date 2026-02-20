package com.aptoide.android.aptoidegames.feature_rtb.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.randomBundle
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.feature_rtb.data.RTBApp
import com.aptoide.android.aptoidegames.feature_rtb.data.RTBAppsListUiState
import com.aptoide.android.aptoidegames.home.BundleHeader
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.mmp.UTMContext
import com.aptoide.android.aptoidegames.mmp.WithUTM
import com.aptoide.android.aptoidegames.theme.AptoideTheme

private var hasSentImpression = false

@Composable
fun RTBAptoideMMPController(
  apps: List<RTBApp>,
) {
  apps.forEachIndexed { index, rtbApp ->
    val app = rtbApp.app
    if (!hasSentImpression) {
      app.campaigns?.toAptoideMMPCampaign()
        ?.sendImpressionEvent(UTMContext.current, app.packageName)
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
  WithUTM(
    medium = "rtb",
    campaign = "regular",
    content = "home-bundle",
    navigate = navigate,
    shouldSendClickEvents = true
  ) { navigate ->
    RTBSectionViewContent(
      bundle = bundle,
      navigate = navigate,
      spaceBy = spaceBy,
      onShowLoading = onShowLoading
    )
  }
}

@Composable
private fun RTBSectionViewContent(
  bundle: Bundle,
  navigate: (String) -> Unit,
  spaceBy: Int = 0,
  onShowLoading: (Boolean) -> Unit = {}
) {
  val (uiState, _) = rememberRTBApps("home-bundle", bundle.timestamp)

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
}

@Composable
fun RTBBundleView(
  navigate: (String) -> Unit,
  apps: List<RTBApp>,
  onShowLoading: (Boolean) -> Unit = {}
) {
  val homeApps = apps.take(9)
  RTBAptoideMMPController(homeApps)
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
