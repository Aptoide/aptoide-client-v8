package com.aptoide.android.aptoidegames.feature_payments.analytics

import com.appcoins.payments.analytics.Logger
import timber.log.Timber
import javax.inject.Inject

class AGLogger @Inject constructor() : Logger {
  override fun logEvent(
    tag: String,
    message: String,
    data: Map<String, Any?>,
  ) {
    Timber.tag(tag).i(message)
    data.entries.forEach {
      Timber.tag(tag).i("  ${it.key}: ${it.value.toString()}")
    }
  }

  override fun logError(
    tag: String,
    throwable: Throwable,
  ) {
    Timber.tag(tag).e(throwable)
  }
}
