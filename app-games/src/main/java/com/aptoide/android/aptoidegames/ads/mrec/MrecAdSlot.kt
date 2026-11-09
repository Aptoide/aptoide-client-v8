package com.aptoide.android.aptoidegames.ads.mrec

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdUiState
import com.aptoide.android.aptoidegames.ads.search.SearchAdSlotViewModel
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

/** Width and height of a medium rectangle, the only size MAX serves for the MREC format. */
const val MREC_WIDTH_DP = 300
const val MREC_HEIGHT_DP = 250

/**
 * The Search MREC treatment. The controller and the [com.applovin.mediation.ads.MaxAdView] are
 * owned by the Search landing ViewModel ([SearchAdSlotViewModel.MrecArm]), so they live exactly
 * as long as the native arm's ViewModel; this composable only hosts the view and forwards the
 * shared near-viewport + resumed [active] signal. The same gates, bounded retries, pause rules
 * and disposal as every native slot apply through the shared controller. Takes no space until an
 * ad has loaded; an ineligible or failed slot shows nothing and never falls back to native.
 */
@Composable
fun MrecAdSlot(
  arm: SearchAdSlotViewModel.MrecArm,
  active: Boolean,
  modifier: Modifier = Modifier,
) {
  val controller = arm.controller
  LaunchedEffect(controller, active) {
    controller.setActive(active)
    if (active) controller.requestLoad()
  }

  val state by controller.uiState.collectAsState()
  val loaded = state as? NativeAdUiState.Loaded ?: return
  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = stringResource(R.string.native_ad_title),
      style = AGTypography.InputsS,
      color = Palette.GreyLight,
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
    )
    AndroidView(
      modifier = Modifier.size(MREC_WIDTH_DP.dp, MREC_HEIGHT_DP.dp),
      factory = { context ->
        // Re-hosting after the item left composition: the view is detached from its old parent
        // first. Attaching is the "rendered" signal, not a viewability claim.
        val adView = arm.loaderFactory.detachedView()
        if (adView != null) {
          controller.bind(loaded.ad) { true }
          adView
        } else {
          android.widget.FrameLayout(context)
        }
      },
    )
  }
}
