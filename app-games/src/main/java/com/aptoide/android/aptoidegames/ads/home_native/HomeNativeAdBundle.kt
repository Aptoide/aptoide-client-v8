package com.aptoide.android.aptoidegames.ads.home_native

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
 * The native ad rendered as a home bundle: same header row as every other bundle, the ad card
 * below it, and the same 32dp gap the other bundles use.
 */
@Composable
fun HomeNativeAdBundle(
  onRender: (MaxNativeAdView) -> Unit,
  spaceBy: Int = 32,
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    BundleHeader(
      title = stringResource(R.string.home_native_ad_title),
      icon = null,
      hasMoreAction = false,
    )
    AndroidView(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      factory = { context ->
        MaxNativeAdView(homeNativeAdViewBinder(), context).also(onRender)
      },
      onRelease = { view -> view.recycle() },
    )
    Spacer(modifier = Modifier.height(spaceBy.dp))
  }
}

/** Manual native template: binds the MAX asset ids to the views in home_native_ad_view.xml. */
internal fun homeNativeAdViewBinder(): MaxNativeAdViewBinder =
  MaxNativeAdViewBinder.Builder(R.layout.home_native_ad_view)
    .setTitleTextViewId(R.id.home_native_ad_title)
    .setBodyTextViewId(R.id.home_native_ad_body)
    .setAdvertiserTextViewId(R.id.home_native_ad_advertiser)
    .setIconImageViewId(R.id.home_native_ad_icon)
    .setMediaContentViewGroupId(R.id.home_native_ad_media)
    .setOptionsContentViewGroupId(R.id.home_native_ad_options)
    .setStarRatingContentViewGroupId(R.id.home_native_ad_stars)
    .setCallToActionButtonId(R.id.home_native_ad_cta)
    .build()
