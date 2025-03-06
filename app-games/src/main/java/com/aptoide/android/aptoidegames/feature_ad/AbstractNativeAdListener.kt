package com.aptoide.android.aptoidegames.feature_ad

import com.mbridge.msdk.out.Campaign
import com.mbridge.msdk.out.Frame
import com.mbridge.msdk.out.NativeListener.NativeAdListener

interface AbstractNativeAdListener : NativeAdListener {
  override fun onAdLoaded(campaigns: List<Campaign?>?, totalCount: Int) {
  }

  override fun onAdLoadError(errorMsg: String) {
  }

  override fun onAdClick(campaign: Campaign) {
  }

  override fun onAdFramesLoaded(frames: List<Frame?>?) {
  }

  override fun onLoggingImpression(impression: Int) {
  }
}
