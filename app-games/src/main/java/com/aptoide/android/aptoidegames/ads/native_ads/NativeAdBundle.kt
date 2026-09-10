package com.aptoide.android.aptoidegames.ads.native_ads

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.home.BundleHeader

/**
 * A native ad rendered with the app's bundle anatomy: the same header row as every other bundle,
 * the ad card below it, and a trailing gap. Used by every [NativeAdPlacement].
 */
@Composable
fun NativeAdBundle(
  onRender: (MaxNativeAdView) -> Unit,
  modifier: Modifier = Modifier,
  spaceBy: Int = 32,
) {
  Column(modifier = modifier.fillMaxWidth()) {
    BundleHeader(
      title = stringResource(R.string.native_ad_title),
      icon = null,
      hasMoreAction = false,
    )
    AndroidView(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      factory = { context ->
        MaxNativeAdView(nativeAdViewBinder(), context).also(onRender)
      },
      onRelease = { view -> view.recycle() },
    )
    Spacer(modifier = Modifier.height(spaceBy.dp))
  }
}

/**
 * A native ad slot for long, non-lazy pages: takes no space until an ad is loaded, and only asks
 * [viewModel] to load once the slot comes within [preloadDistanceDp] of the bottom of the viewport.
 * Pages that are left before the user scrolls that far never request an ad.
 */
@Composable
fun NativeAdSlot(
  viewModel: NativeAdViewModel,
  modifier: Modifier = Modifier,
  preloadDistanceDp: Int = 400,
) {
  val state by viewModel.uiState.collectAsState()
  val density = LocalDensity.current
  val screenHeightPx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
  val preloadDistancePx = with(density) { preloadDistanceDp.dp.toPx() }
  var slotTopPx by remember { mutableFloatStateOf(Float.MAX_VALUE) }

  LaunchedEffect(slotTopPx) {
    if (slotTopPx < screenHeightPx + preloadDistancePx) viewModel.startLoading()
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .onGloballyPositioned { coordinates -> slotTopPx = coordinates.positionInRoot().y },
  ) {
    if (state is NativeAdUiState.Loaded) {
      NativeAdBundle(onRender = viewModel::render)
    }
  }
}

/** Manual native template: binds the MAX asset ids to the views in native_ad_view.xml. */
internal fun nativeAdViewBinder(): MaxNativeAdViewBinder =
  MaxNativeAdViewBinder.Builder(R.layout.native_ad_view)
    .setTitleTextViewId(R.id.native_ad_title)
    .setBodyTextViewId(R.id.native_ad_body)
    .setAdvertiserTextViewId(R.id.native_ad_advertiser)
    .setIconImageViewId(R.id.native_ad_icon)
    .setMediaContentViewGroupId(R.id.native_ad_media)
    .setOptionsContentViewGroupId(R.id.native_ad_options)
    .setStarRatingContentViewGroupId(R.id.native_ad_stars)
    .setCallToActionButtonId(R.id.native_ad_cta)
    .build()
