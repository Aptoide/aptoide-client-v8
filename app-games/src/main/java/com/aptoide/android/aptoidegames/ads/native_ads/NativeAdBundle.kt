package com.aptoide.android.aptoidegames.ads.native_ads

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
