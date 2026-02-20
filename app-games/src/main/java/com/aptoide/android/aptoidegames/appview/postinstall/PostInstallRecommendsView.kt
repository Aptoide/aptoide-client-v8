package com.aptoide.android.aptoidegames.appview.postinstall

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.dto.BundleMeta
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsBundleMeta
import com.aptoide.android.aptoidegames.feature_rtb.data.RTBAppsListUiState
import com.aptoide.android.aptoidegames.feature_rtb.presentation.rememberRTBAdClickHandler
import com.aptoide.android.aptoidegames.feature_rtb.presentation.rememberRTBApps
import com.aptoide.android.aptoidegames.mmp.UTMContext
import com.aptoide.android.aptoidegames.mmp.WithUTM
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

private const val POST_INSTALL_TAG = "appview-post-install"

@Composable
fun PostInstallRecommendsView(
  navigate: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  OverrideAnalyticsBundleMeta(
    bundleMeta = BundleMeta(
      tag = "rtb-$POST_INSTALL_TAG",
      bundleSource = "rtb"
    ),
    navigate = navigate,
  ) { navigate ->
    WithUTM(
      medium = "rtb",
      campaign = "regular",
      content = POST_INSTALL_TAG,
      navigate = navigate
    ) { navigate ->
      PostInstallRecommendsContent(
        navigate = navigate,
        modifier = modifier,
      )
    }
  }
}

@Composable
private fun PostInstallRecommendsContent(
  navigate: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val timestamp = remember { System.currentTimeMillis().toString() }
  val (uiState, _) = rememberRTBApps(POST_INSTALL_TAG, timestamp)

  when (uiState) {
    is RTBAppsListUiState.Idle -> {
      val apps = uiState.apps.take(9)
      if (apps.isNotEmpty()) {
        val handleRTBAdClick = rememberRTBAdClickHandler(
          rtbAppsList = apps,
          navigate = navigate,
        )

        val utmContext = UTMContext.current
        val lazyListState = rememberLazyListState()

        val impressionsSent = rememberSaveable(
          saver = listSaver(
            save = { it.toList() },
            restore = { it.toMutableSet() }
          )
        ) { mutableSetOf<Int>() }
        LaunchedEffect(lazyListState) {
          snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.map { it.index } }
            .collect { visibleIndices ->
              visibleIndices.forEach { index ->
                if (index !in impressionsSent) {
                  impressionsSent.add(index)
                  apps.getOrNull(index)?.app?.let { app ->
                    app.campaigns?.toAptoideMMPCampaign()
                      ?.sendImpressionEvent(utmContext, app.packageName)
                  }
                }
              }
            }
        }

        Column(
          modifier = modifier
            .fillMaxWidth()
            .border(
              width = 4.dp,
              color = Palette.GreyDark,
              shape = RectangleShape
            )
        ) {
            // Header
            Row(
              modifier = Modifier
                .background(Palette.GreyDark)
                .padding(horizontal = 8.dp, vertical = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = stringResource(R.string.post_install_suggested_title),
                style = AGTypography.BodyBold,
                color = Palette.GreyLight
              )
              Text(
                text = stringResource(R.string.post_install_sponsored_label),
                style = AGTypography.InputsXXS,
                color = Palette.GreyLight
              )
            }
            // App cards row
            LazyRow(
              modifier = Modifier
                .semantics {
                  collectionInfo = CollectionInfo(1, apps.size)
                }
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 16.dp),
              state = lazyListState,
              contentPadding = PaddingValues(horizontal = 16.dp),
              horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
              itemsIndexed(apps) { index, rtbApp ->
                val app = rtbApp.app
                PostInstallAppCard(
                  app = app,
                  onClick = {
                    app.campaigns?.toAptoideMMPCampaign()?.sendClickEvent(utmContext)
                    handleRTBAdClick(app.packageName, index)
                  },
                )
              }
            }
          }
        }
      }

    RTBAppsListUiState.Loading,
    RTBAppsListUiState.Empty,
    RTBAppsListUiState.Error,
    RTBAppsListUiState.NoConnection,
      -> Unit
  }
}

@PreviewDark
@Composable
private fun PostInstallRecommendsViewPreview() {
  AptoideTheme {
    PostInstallRecommendsView(
      navigate = {},
      modifier = Modifier.padding(16.dp),
    )
  }
}
