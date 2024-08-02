package com.aptoide.android.aptoidegames.analytics

import android.os.Bundle
import androidx.annotation.Size

interface AnalyticsSender {
  fun setUserProperty(
    @Size(min = 1L, max = 24L) name: String,
    @Size(max = 36L) value: String?,
  ) {
  }

  fun logEvent(
    @Size(min = 1L, max = 40L) name: String,
    params: Bundle?,
  ) {
  }
}
