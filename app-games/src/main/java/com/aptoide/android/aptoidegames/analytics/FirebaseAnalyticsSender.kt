package com.aptoide.android.aptoidegames.analytics

import androidx.annotation.Size
import androidx.core.os.bundleOf
import com.aptoide.android.aptoidegames.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsSender @Inject constructor(
  private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsSender {
  override fun setUserProperties(vararg props: Pair<String, Any?>) = props.forEach {
    if (BuildConfig.DEBUG) {
      Timber.tag("GA").i("set UP  ${it.first} = ${it.second}")
    }
    firebaseAnalytics.setUserProperty(it.first, it.second?.toString())
  }

  override fun logEvent(
    @Size(min = 1L, max = 40L) name: String,
    params: Map<String, Any?>?,
  ) {
    if (BuildConfig.DEBUG) {
      Timber.tag("GA").i(name)
      params?.entries?.forEach {
        Timber.tag("GA").i("  ${it.key}: ${it.value.toString()}")
      }
    }
    firebaseAnalytics.logEvent(name, params?.let { bundleOf(*it.toList().toTypedArray()) })
  }
}
