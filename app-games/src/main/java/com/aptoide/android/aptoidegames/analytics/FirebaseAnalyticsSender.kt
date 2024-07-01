package com.aptoide.android.aptoidegames.analytics

import androidx.annotation.Size
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsSender @Inject constructor(
  private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsSender {
  override fun setUserProperties(vararg props: Pair<String, Any?>) = props.forEach {
    firebaseAnalytics.setUserProperty(it.first, it.second?.toString())
  }

  override fun logEvent(
    @Size(min = 1L, max = 40L) name: String,
    params: Map<String, Any?>?,
  ) = firebaseAnalytics.logEvent(name, params?.let { bundleOf(*it.toList().toTypedArray()) })
}
