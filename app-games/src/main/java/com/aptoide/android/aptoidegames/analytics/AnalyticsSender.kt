package com.aptoide.android.aptoidegames.analytics

import androidx.annotation.Size

interface AnalyticsSender {
  fun setUserProperties(vararg props: Pair<String, Any?>) {}

  fun logEvent(
    @Size(min = 1L, max = 40L) name: String,
    params: Map<String, Any?>?,
  ) {
  }
}
