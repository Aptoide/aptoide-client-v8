package com.aptoide.android.aptoidegames.analytics

import android.os.Bundle
import androidx.annotation.Size
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsSender @Inject constructor(
  private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsSender {
  override fun setUserProperty(
    @Size(min = 1L, max = 24L) name: String,
    @Size(max = 36L) value: String?,
  ) = firebaseAnalytics.setUserProperty(name, value)

  override fun logEvent(
    @Size(min = 1L, max = 40L) name: String,
    params: Bundle?,
  ) = firebaseAnalytics.logEvent(name, params)
}
