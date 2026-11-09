package com.aptoide.android.aptoidegames.ads.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.ads.mrec.MrecAdSlot
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdBundle
import com.aptoide.android.aptoidegames.ads.native_ads.NativeAdUiState
import com.aptoide.android.aptoidegames.ads.native_ads.rememberHostResumed
import com.aptoide.android.aptoidegames.ads.native_ads.rememberSearchNativeAd
import com.aptoide.android.aptoidegames.ads.native_ads.trackNearViewport

/**
 * The single ad opportunity of the Search landing, shared by both formats under test. The item is
 * always present once the assignment is known (zero height until an ad loads) and measures its
 * own layout, so native and MREC are activated by the same explicit signal: near the viewport
 * right now AND host resumed. Scrolling away or backgrounding deactivates either format, which
 * defers retries (native) and stops retries and refresh (MREC); only the assigned format's slot
 * is composed, so the other format is never created or requested.
 */
@Composable
fun SearchAdOpportunity(
  decision: SearchAdDecision,
  slot: SearchAdSlotViewModel,
  modifier: Modifier = Modifier,
) {
  var near by remember { mutableStateOf(false) }
  val resumed = rememberHostResumed()
  val active = near && resumed
  // A lazy item that is disposed is no longer near, whatever the last layout said: both arms are
  // deactivated (their controllers outlive the item in their ViewModels).
  DisposableEffect(decision, slot) {
    onDispose {
      near = false
      slot.mrecArm(decision)?.controller?.setActive(false)
    }
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .trackNearViewport { near = it },
  ) {
    if (decision.requestsNative) {
      SearchNativeAdSlot(active = active)
    } else {
      slot.mrecArm(decision)?.let { arm ->
        MrecAdSlot(
          arm = arm,
          active = active,
          modifier = Modifier.padding(top = 24.dp, bottom = 32.dp),
        )
      }
    }
  }
}

@Composable
private fun SearchNativeAdSlot(active: Boolean) {
  val viewModel = rememberSearchNativeAd()
  val loaded = viewModel.uiState.collectAsState().value as? NativeAdUiState.Loaded

  LaunchedEffect(viewModel, active) {
    viewModel.setSlotActive(active)
    if (active) viewModel.startLoading()
  }
  DisposableEffect(viewModel) { onDispose { viewModel.setSlotActive(false) } }

  loaded?.let {
    NativeAdBundle(
      ad = it.ad,
      template = viewModel.placement.template,
      onRender = viewModel::render,
      modifier = Modifier.padding(top = 24.dp),
    )
  }
}
