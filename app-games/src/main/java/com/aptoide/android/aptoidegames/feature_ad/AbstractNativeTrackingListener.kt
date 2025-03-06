package com.aptoide.android.aptoidegames.feature_ad

import com.mbridge.msdk.out.Campaign
import com.mbridge.msdk.out.NativeListener.NativeTrackingListener

interface AbstractNativeTrackingListener : NativeTrackingListener {
  override fun onFinishRedirection(campaign: Campaign?, redirectUrl: String?) {
  }

  override fun onRedirectionFailed(p0: Campaign?, p1: String?) {
  }

  override fun onStartRedirection(campaign: Campaign?, redirectUrl: String?) {
  }

  override fun onDismissLoading(campaign: Campaign?) {
  }

  override fun onDownloadFinish(campaign: Campaign?) {
  }

  override fun onDownloadProgress(progress: Int) {
  }

  override fun onDownloadStart(campaign: Campaign?) {
  }

  override fun onInterceptDefaultLoadingDialog(): Boolean {
    return true
  }

  override fun onShowLoading(campaign: Campaign?) {
  }
}
